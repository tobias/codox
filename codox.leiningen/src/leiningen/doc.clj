(ns leiningen.doc
  (:refer-clojure :exclude [doc])
  (:require [leinjacker.deps :as deps]
            [leinjacker.eval :as eval]
            [leiningen.core.project :as project]))

(defn- get-options [project]
  (-> project
      (select-keys [:name :version :description])
      (merge {:sources (:source-paths project ["src"])
              :root    (:root project)}
             (:codox project))))

(defn doc
  "Generate API documentation from source code."
  [project]
  (let [project (if (get-in project [:profiles :codox])
                  (project/merge-profiles project [:codox])
                  project)]
    (eval/eval-in-project
     (deps/add-if-missing project '[codox/codox.core "0.8.0"])
     `(codox.main/generate-docs
       (update-in '~(get-options project) [:src-uri-mapping] eval))
     `(require 'codox.main))))
