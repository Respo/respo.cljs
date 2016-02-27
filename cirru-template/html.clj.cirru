
require $ quote
  [] hiccup.core :refer $ [] html

defn render (data) $ html
  [] :html
    [] :head ([] :title |Respo)
      [] :link $ {} (:type |text/css)
        :href |css/style.css
        :rel |stylesheet

      [] :link $ {} (:type |image/png)
        :rel |icon
        :href |images/frp.png

      [] :style nil "|body * {box-sizing: border-box; scroll-behavior: smooth; }"

    [] :body ([] :div#app) $ [] :script
      {} $ :src |cljs/main.js
