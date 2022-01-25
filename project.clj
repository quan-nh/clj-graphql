(defproject clj-graphql "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.11.0-alpha4"]
                 [org.clojure/core.async "1.5.648"]
                 [com.stuartsierra/component "1.0.0"]
                 [com.walmartlabs/lacinia-pedestal "1.1"]
                 [com.github.seancorfield/next.jdbc "1.2.761"]
                 [org.postgresql/postgresql "42.3.1"]
                 [com.zaxxer/HikariCP "5.0.1"]
                 [io.aviso/logging "1.0"]]
  :main ^:skip-aot clj-graphql.system
  :target-path "target/%s"
  :profiles {:dev     {:source-paths ["dev"]
                       :dependencies [[com.stuartsierra/component.repl "0.2.0"]]}
             :uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :repl-options {:init-ns user})
