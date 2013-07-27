package org.github.raelg.module

import com.google.inject.{Binder, Module}
import com.google.inject.name.Names
import org.github.raelg.utils.PropertyUtils

/**
 * Created with IntelliJ IDEA.
 * User: rael
 * Date: 26/07/2013
 * Time: 13:43
 */
class ModuleLoader extends Module {

    def configure(binder: Binder) {
        Names.bindProperties(binder, PropertyUtils.getProperties("project.properties"))
    }
}
