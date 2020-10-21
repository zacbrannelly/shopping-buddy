package com.zacbrannelly.shoppingbuddy.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zacbrannelly.shoppingbuddy.R

class ImageOptionsDialogFragment(
    private val onTakePicture: () -> Unit,
    private val onFromGallery: () -> Unit,
    private val onFromUrl: () -> Unit): BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.modal_select_image_options, container)

        val takePictureButton = view.findViewById<View>(R.id.take_picture_button)
        val fromGalleryButton = view.findViewById<View>(R.id.select_gallery_button)
        val fromUrlButton = view.findViewById<View>(R.id.from_link_button)

        takePictureButton.setOnClickListener { onTakePicture(); dismiss() }
        fromGalleryButton.setOnClickListener { onFromGallery(); dismiss() }
        fromUrlButton.setOnClickListener { onFromUrl(); dismiss() }

        return view
    }
}