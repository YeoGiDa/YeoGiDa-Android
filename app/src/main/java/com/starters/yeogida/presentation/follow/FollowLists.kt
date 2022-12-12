package com.starters.yeogida.presentation.follow

import com.starters.yeogida.data.local.FollowUserData

object FollowLists {
    val follower = mutableListOf(
        FollowUserData(
            "https://user-images.githubusercontent.com/20774764/153292680-cac43d23-3621-4cce-97b0-38db57d60aa0.png",
            "user1"
        ),
        FollowUserData(
            "https://user-images.githubusercontent.com/20774764/153292685-e8ccb8df-cd94-4135-b472-c3d48e477202.png",
            "user2"
        )
    )
    val following = mutableListOf(
        FollowUserData(
            "https://user-images.githubusercontent.com/20774764/153292676-d3b24c31-f4ba-4273-9233-a0c77f835fd7.png",
            "user3"
        ),
        FollowUserData(
            "https://user-images.githubusercontent.com/20774764/153292675-78a7108b-644e-4836-ad30-dd261d998e4c.png",
            "user4"
        )
    )
}