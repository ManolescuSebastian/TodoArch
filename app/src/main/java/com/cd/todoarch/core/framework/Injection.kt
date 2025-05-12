package com.cd.todoarch.core.framework

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/*
 * Using the Injection framework:
 *
 * Create bindings somewhere in the application before the injections would occur.
 *
 * factory<MyInterface>(named = "specialName") { SomeImplementation() }
 * factory<OtherInterface> { doSomethingHereToCreateAnInstance() }
 * single { AnotherClass() }
 * single(named = "specialString") { "Something special" }
 *
 * Use injection:
 *
 * class SomeClass {
 *   private val dependency_one by inject<MyInterface>(named = "specialName")
 *   private val dependency_two: OtherInterface by inject()
 *   private val anotherClass by inject<AnotherClass>()
 *   private val test: String by inject(named = "specialString")
 *   ...
 * }
 */

val injectionFactories = mutableMapOf<Pair<KClass<out Any>, String?>, () -> Any>()

inline fun <reified T : Any> injectValue(named: String? = null) =
    injectionFactories.getValue(T::class to named).invoke() as T

inline fun <reified T : Any> inject(named: String? = null) =
    object : ReadOnlyProperty<Any, T> {
        private val value: T by lazy { injectValue(named) }
        override fun getValue(thisRef: Any, property: KProperty<*>): T = value
    }

inline fun <reified T : Any> factory(named: String? = null, noinline block: () -> T) {
    injectionFactories[T::class to named] = block
}

inline fun <reified T : Any> single(named: String? = null, noinline block: () -> T) {
    block.invoke().let { factory(named) { it } }
}
