package com.example.thenoteapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Query
import com.example.thenoteapp.MainActivity
import com.example.thenoteapp.R
import com.example.thenoteapp.adapter.NoteAdapter
import com.example.thenoteapp.databinding.FragmentHomeBinding
import com.example.thenoteapp.model.Note
import com.example.thenoteapp.viewmodel.NoteViewModel


class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider {
    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var noteViewModel : NoteViewModel
    // oya NoteViewModel heduwa na na ekai error view model folder eke hadhnne one
    private lateinit var noteAdapter : NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        noteViewModel = (activity as MainActivity).noteViewModel
        setupHomeRecyclerView()


        binding.addNoteFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
        }
    }

    private fun updateUI(note: List<Note>){
        if (note != null){
            if (note.isNotEmpty()){
                binding.emptyNotesImage.visibility =View.GONE
                binding.homeRecyclerView.visibility =View.VISIBLE
            }else{
                binding.emptyNotesImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setupHomeRecyclerView(){
        noteAdapter = NoteAdapter()
        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }
        activity?.let {
            noteViewModel.getAllNotes().observe(viewLifecycleOwner){ note ->
                noteAdapter.differ.submitList(note)
                updateUI(note)
            }
        }
    }

    private fun searchNote(query: String?){
        val searchQuery = "%$query"
// bossa oyage githb accnt ekta ynnko
        noteViewModel.searchNote(searchQuery).observe(this){ List ->
            noteAdapter.differ.submitList(List)

        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean{
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean{
        if(newText != null){
            searchNote(newText)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }


}