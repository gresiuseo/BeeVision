package com.beevision.app.model

data class FrameMap(
    val rows: Int = 3,
    val cols: Int = 3,
    val cells: List<FrameCell>
)