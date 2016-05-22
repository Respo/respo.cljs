
ns respo.util.error

defn raise (x)
  throw $ js/Error. x
