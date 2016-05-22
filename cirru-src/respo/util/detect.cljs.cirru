
ns respo.util.detect
  :require
    [] respo.alias :refer $ [] Component Element

defn component? (x)
  = Component (type x)

defn element? (x)
  = Element (type x)
