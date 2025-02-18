package com.xfei33.quicktodo.ui.main

// 定义所有导航路由
object NavRoutes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val MAIN = "main"

    // 主界面内的路由
    object Main {
        const val TODO = "todo"
        const val FOCUS = "focus"
        const val MESSAGE = "message"
        const val MESSAGE_DETAIL = "message_detail/{messageId}"
        const val PROFILE = "profile"
        
        fun messageDetail(messageId: String) = "message_detail/$messageId"
    }
}