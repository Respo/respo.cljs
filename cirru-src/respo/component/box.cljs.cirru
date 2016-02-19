
ns respo.component.box-demo $ :require
  [] respo.component.card-demo :refer $ [] card-demo

def box-demo $ {}
  :initial-state $ {}
  :render $ fn (props state)
    fn (intent) $ let
      (tag :demo)
      [] :div ({})
        [] :h5 ({}) "|here's a demo"
        [] card-demo $ {} :tag tag
