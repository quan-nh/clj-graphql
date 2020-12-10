(ns clj-graphql.server
  (:require [buddy.auth.backends :as auth.backends]
            [buddy.auth.middleware :as auth.middleware]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.pedestal :refer [inject]]
            [com.walmartlabs.lacinia.pedestal2 :as p2]
            [io.pedestal.http :as http]
            [io.pedestal.interceptor :refer [interceptor]]
            [io.pedestal.interceptor.chain :as interceptor.chain]
            [io.pedestal.interceptor.error :refer [error-dispatch]]))

(def users
  "A sample user store."
  {:admin "secret"
   :test  "secret"})

(def basic-auth-backend
  "A buddy-auth Basic Authentication backend.  See
  https://funcool.github.io/buddy-auth/latest/#http-basic"
  (auth.backends/basic {:realm  "MyApi"
                        :authfn (fn [request {:keys [username password]}]
                                  (when-let [user-password (get users (keyword username))]
                                    (when (= password user-password)
                                      (keyword username))))}))

(defn authentication-interceptor
  "Port of buddy-auth's wrap-authentication middleware."
  [backend]
  (interceptor
    {:name  ::authenticate
     :enter (fn [ctx]
              (update ctx :request auth.middleware/authentication-request backend))}))

(def user-info-interceptor
  (interceptor
    {:name  ::user-info
     :enter (fn [context]
              (let [{:keys [request]} context
                    user-info (:identity request)]
                (assoc-in context [:request :lacinia-app-context :user-info] user-info)))}))

(defn ^:private interceptors
  [schema]
  (-> (p2/default-interceptors schema nil)
      (inject (authentication-interceptor basic-auth-backend) :before ::p2/inject-app-context)
      (inject user-info-interceptor :after ::p2/inject-app-context)))

(defrecord Server [schema-provider server]
  component/Lifecycle
  (start [this]
    (if server
      this
      (let [compiled-schema (:schema schema-provider)
            interceptors    (interceptors compiled-schema)
            routes          (into #{["/api" :post interceptors :route-name ::graphql-api]
                                    ["/ide" :get (p2/graphiql-ide-handler nil) :route-name ::graphiql-ide]}
                                  (p2/graphiql-asset-routes "/assets/graphiql"))]
        (assoc this :server (-> {:env          :dev
                                 ::http/routes routes
                                 ::http/port   8888
                                 ::http/type   :jetty
                                 ::http/join?  false}
                                p2/enable-graphiql
                                (p2/enable-subscriptions compiled-schema nil)
                                http/create-server
                                http/start)))))

  (stop [this]
    (when server (http/stop server))
    (assoc this :server nil)))

(defn new-server [] (map->Server {}))
