
import sys, os
sys.path.append(os.path.abspath("streampipes-wrapper-python"))

from streampipes.model.base.AbstractStreamPipesEntity import AbstractStreamPipesEntity
class UnnamedStreamPipesEntity(AbstractStreamPipesEntity):

  __serialVersionUID = 8051137255998890188
  def __init__(self):
      pass
  @classmethod
  def with_elementId(elementId: str):
    self.elementId = elementId;