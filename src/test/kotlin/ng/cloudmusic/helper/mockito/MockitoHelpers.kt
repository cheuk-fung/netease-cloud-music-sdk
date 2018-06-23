package ng.cloudmusic.helper.mockito

import org.mockito.Mockito

inline fun <reified T> any(): T = Mockito.any<T>()
