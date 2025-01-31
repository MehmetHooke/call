package com.example.pianosense

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {

    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicList: List<Music>
    private lateinit var filteredList: MutableList<Music>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val searchRecyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        val suggestedMusics = view.findViewById<TextView>(R.id.suggestedMusics)

        // Anasayfada tanımladığımız müzik listesini alıyoruz
        musicList = listOf(
            Music(1, "Valse", "Evgeny Grinko", R.drawable.avatar),
            Music(2, "Beauty", "Jon Cossack", R.drawable.avatar),
            Music(3, "Paragon Sky", "Glenn Natale", R.drawable.avatar)
        )
        filteredList = musicList.toMutableList()

        // RecyclerView ve Adapter kurulumu
        musicAdapter = MusicAdapter(requireContext(), filteredList) { selectedMusic ->
            // Müziğe tıklandığında yapılacak işlemler
            (activity as? MainActivity)?.navigateToPlayFragment(
                selectedMusic.id,
                selectedMusic.title,
                selectedMusic.composer,
                selectedMusic.imageResId
            )
        }
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.adapter = musicAdapter

        // searchEditText'e odaklandığında önerilen müzikleri gizlemek için listener ekliyoruz
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                suggestedMusics.visibility = View.GONE
            } else if (!hasFocus && searchEditText.text.isEmpty()) {
                suggestedMusics.visibility = View.VISIBLE
            }
        }

        // Arama çubuğuna TextWatcher ekliyoruz
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMusicList(s.toString())
                suggestedMusics.visibility = if (s.isNullOrEmpty()) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Arama sonuçlarını filtreleme fonksiyonu
    private fun filterMusicList(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(musicList)
        } else {
            val lowercaseQuery = query.lowercase()
            filteredList.addAll(musicList.filter {
                it.title.lowercase().contains(lowercaseQuery) ||
                        it.composer.lowercase().contains(lowercaseQuery)
            })
        }
        musicAdapter.notifyDataSetChanged()
    }
}
