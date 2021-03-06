package org.secfirst.umbrella.feature.segment.view

import org.secfirst.umbrella.data.database.segment.Markdown

open class SegmentItem(onClickSegment: (Int) -> Unit,
                       onSegmentShareClick: (Markdown) -> Unit,
                       onSegmentFavoriteClick: (Markdown) -> Unit,
                       markdown: Markdown?) : SegmentCard(onClickSegment, onSegmentShareClick, onSegmentFavoriteClick, markdown) {

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 2
}