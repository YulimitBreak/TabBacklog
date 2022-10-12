package entity.error

class CompositionLocalError(componentName: String) :
    IllegalArgumentException("CompositionLocal of $componentName not present")