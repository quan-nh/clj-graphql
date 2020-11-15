(defproject clj-graphql "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.stuartsierra/component "1.0.0"]
                 [com.walmartlabs/lacinia-pedestal "0.15.0-alpha-2"]]
  :main ^:skip-aot clj-graphql.system
  :target-path "target/%s"
  :profiles {:dev     {:source-paths ["dev"]
                       :dependencies [[com.stuartsierra/component.repl "0.2.0"]]}
             :uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :repl-options {:init-ns user})
