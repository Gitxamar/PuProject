package cargill.com.purina.dashboard.View.ProductCatalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import cargill.com.purina.R
import kotlinx.android.synthetic.main.animal_item.*
import kotlinx.android.synthetic.main.fragment_detail_catalogue.*

class FragmentProductDetail : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_catalogue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expandDescription.setText("Expandable Text View is an android library that allows the users to create the text view which can expand and collapse to read the text description. I bet you guys have seen this in a lot of android applications but might not know the name and its purpose. Well, below is a screenshot of the Instagram application on the Play store. This feature saves a lot of space, rather than laying out the huge chunks of information and occupying the entire page we can further use this option and can utilize the space")
        expandBenefits.setText("Expandable Text View is an android library that allows the users to create the text view which can expand and collapse to read the text description. I bet you guys have seen this in a lot of android applications but might not know the name and its purpose. Well, below is a screenshot of the Instagram application on the Play store. This feature saves a lot of space, rather than laying out the huge chunks of information and occupying the entire page we can further use this option and can utilize the space")
        expandingredients.setText("Expandable Text View is an android library that allows the users to create the text view which can expand and collapse to read the text description. I bet you guys have seen this in a lot of android applications but might not know the name and its purpose. Well, below is a screenshot of the Instagram application on the Play store. This feature saves a lot of space, rather than laying out the huge chunks of information and occupying the entire page we can further use this option and can utilize the space")
    }
}