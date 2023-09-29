(ns monkey.oci.fn.core
  "Core functionality for the OCI Fnproject API client.  The functions here
   mostly just delegate to Martian for performing the actual http calls."
  (:require [martian.core :as martian]
            [monkey.oci.common
             [martian :as cm]
             [utils :as u]]
            [schema.core :as s]))

(def json #{"application/json"})

(def application-routes
  [{:route-name :list-applications
    :method :get
    :query-schema {:compartmentId s/Str
                   (s/optional-key :displayName) s/Str
                   (s/optional-key :id) s/Str}
    :produces json}])

(def routes (concat application-routes))

(def host (comp (partial format "https://functions.%s.oraclecloud.com") :region))

(defn make-context
  "Creates Martian context for the given configuration.  This context
   should be passed to subsequent requests."
  [conf]
  (cm/make-context conf host routes))

(def send-request martian/response-for)

(u/define-endpoints *ns* routes martian/response-for)
