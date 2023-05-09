package com.example.streamcinema

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.streamcinema.model.Actor
import kotlinx.coroutines.launch

class CastActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cast)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var actorList = emptyList<Actor>()
        val movieId = intent.getIntExtra("id", 1)

        lifecycleScope.launch {
            actorList = MoviesData().movieCast(movieId)
            adapter = ActorAdapter(actorList)
            recyclerView.adapter = adapter
        }
    }
}

class ActorAdapter(private val actors: List<Actor>) :
    RecyclerView.Adapter<ActorAdapter.ActorViewHolder>() {

    class ActorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.name_text_view)
        val surnameTextView: TextView = view.findViewById(R.id.surname_text_view)
        val roleTextView: TextView = view.findViewById(R.id.role_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cast_view, parent, false)
        return ActorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val actor = actors[position]
        holder.nameTextView.text = actor.firstName
        holder.surnameTextView.text = actor.lastName
        holder.roleTextView.text = actor.role
    }

    override fun getItemCount() = actors.size
}
