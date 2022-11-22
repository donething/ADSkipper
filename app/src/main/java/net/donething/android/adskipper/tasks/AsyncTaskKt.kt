package net.donething.android.adskipper.tasks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 使用`kotlin协程`异步执行耗时任务
 * @link https://stackoverflow.com/a/62748880/8179418
 */
fun <P, R> CoroutineScope.executeAsyncTask(
    onPreExecute: () -> Unit,
    doInBackground: suspend (suspend (P) -> Unit) -> R,
    onPostExecute: (R) -> Unit,
    onProgressUpdate: (P) -> Unit
) = launch {
    onPreExecute()

    val result = withContext(Dispatchers.IO) {
        doInBackground {
            withContext(Dispatchers.Main) { onProgressUpdate(it) }
        }
    }
    onPostExecute(result)
}