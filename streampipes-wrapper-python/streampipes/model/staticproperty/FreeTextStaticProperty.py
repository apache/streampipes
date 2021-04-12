import sys, os
sys.path.append(os.path.abspath("streampipes-wrapper-python"))

import streampipes.model.staticproperty.StaticProperty 

class FreeTextStaticProperty(StaticProperty):
  __serialVersionUID = 1
  def __init__(self):
    pass