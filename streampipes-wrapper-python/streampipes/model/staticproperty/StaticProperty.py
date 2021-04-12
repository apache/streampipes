import sys, os
sys.path.append(os.path.abspath("streampipes-wrapper-python"))

from streampipes.model.base.UnnamedStreamPipesEntity import UnnamedStreamPipesEntity 

class StaticProperty(UnnamedStreamPipesEntity):
  __serialVersionUID = 2509153122084646025
  
  
  def __init__(self):
      self.__set_StaticPropertyType(staticPropertyType)

  def get_StaticPropertyType(self):
    return staticPropertyType

  def __set_StaticPropertyType(self, staticPropertyType):
    self.staticPropertyType = staticPropertyType

