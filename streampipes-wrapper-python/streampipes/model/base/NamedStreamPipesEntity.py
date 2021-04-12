import sys, os
sys.path.append(os.path.abspath("streampipes-wrapper-python"))

from streampipes.model.base.AbstractStreamPipesEntity import AbstractStreamPipesEntity

class NamedStreamPipesEntity(AbstractStreamPipesEntity):
    def __init__(self):
        self.applicationLinks = []
        self.includedAssets = []
        self.includedLocales = []
