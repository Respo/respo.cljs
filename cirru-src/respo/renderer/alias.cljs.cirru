
ns respo.renderer.alias

defn create-element
  tag-name props & children
  into ([])
    concat ([] tag-name props)
      , children

defn create-component (markup)
  fn (& args)
    into ([])
      concat ([] markup)
        , args

def div $ partial create-element :div

def span $ partial create-element :span

def input $ partial create-element :input

def header $ partial create-element :header

def footer $ partial create-element :footer
