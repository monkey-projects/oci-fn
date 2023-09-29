# OCI Fn library

This is a Clojure lib that provides functions for accessing the [OCI Functions
service API](https://docs.oracle.com/en-us/iaas/api/#/en/functions/20181201/).

It uses [Martian](https://github.com/oliyh/martian) to map routing configuration
to actual functions.

## Usage

Include the lib in your project.
For CLI tools (`deps.edn`):
```clojure
{:com.monkeyprojects/oci-fn {:mvn/version "<version>"}}
```
Or Leiningen:
```clojure
[com.monkeyprojects/oci-fn "<version>"]
```

The functions are in the `monkey.oci.fn.core` namespace.  First of all, you'll
have to create a context.  This must be passed to all functions subsequently.
The config needed to create the context should include all fields necessary
to access the API.  The private key must be a Java PrivateKey object.  You can
parse one using `monkey.oci.common.utils/load-privkey`.

```clojure
(require '[monkey.oci.fn.core :as fc])
(require '[monkey.oci.common.utils :as u])

;; The config is required to access the OCI API
(def config {:user-ocid "my-user-ocid"
             :tenancy-ocid "my-tenancy"
	     :key-fingerprint "some fingerprint"
	     :region "eu-frankfurt-1"
	     :private-key (u/load-privkey "my-key-file")})
	     

(def ctx (fc/make-context config))

;; Do some stuff
@(fc/list-applications ctx {})
; => Functions return a deferred to deref it
```

The functions return the raw response from the API, where the body is parsed from JSON.
This is because sometimes you'll need the headers, e.g. for pagination.  Mostly you'll
just need the `:body` and `:status`.

### Available Endpoints

The endpoints are auto-generated from the routes declared in the [core namespace](src/monkey/oci/fn/core.clj)
and they reflect those declared in the [Fn api documentation](https://docs.oracle.com/en-us/iaas/api/#/en/functions/20181201/).

## License

MIT license, see [LICENSE](LICENSE).
Copyright (c) 2023 by [Monkey Projects BV](https://www.monkeyprojects.be).
