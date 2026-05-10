package com.vidyavahini.app.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vidyavahini.app.R
import com.vidyavahini.app.data.model.Route
import com.vidyavahini.app.data.model.Stop
import com.vidyavahini.app.databinding.BottomSheetRouteSelectionBinding

class RouteBottomSheetFragment(
    private val routes: Map<String, Route>,
    private val listener: RouteSelectionListener
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetRouteSelectionBinding? = null
    private val binding get() = _binding!!
    
    private var filteredRoutes = routes.toList()
    private var selectedRouteId: String? = null

    interface RouteSelectionListener {
        fun onRouteSelected(routeId: String, routeName: String, stopId: String, stopName: String, stopOrder: Int, college: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetRouteSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRouteList()
        setupSearch()
        setupBackAction()
        
        // Focus search for better UX
        binding.etSearch.requestFocus()
    }

    private fun setupRouteList() {
        binding.rvRoutes.layoutManager = LinearLayoutManager(requireContext())
        updateRouteAdapter()
    }

    private fun updateRouteAdapter() {
        binding.rvRoutes.adapter = object : RecyclerView.Adapter<RouteViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route_selection, parent, false)
                return RouteViewHolder(view)
            }

            override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
                val (id, route) = filteredRoutes[position]
                holder.name.text = route.name
                holder.details.text = "${route.routeNumber} • ${route.college}"
                holder.itemView.setOnClickListener {
                    showStopSelection(id, route)
                }
            }

            override fun getItemCount() = filteredRoutes.size
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase()
                filteredRoutes = routes.toList().filter { 
                    it.second.name.lowercase().contains(query) || 
                    it.second.routeNumber.lowercase().contains(query) 
                }
                updateRouteAdapter()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun showStopSelection(routeId: String, route: Route) {
        selectedRouteId = routeId
        binding.tvTitle.text = "Select Boarding Stop"
        binding.tvSubtitle.text = "Choose the stop where you'll board the bus"
        binding.tilSearch.visibility = View.GONE
        binding.rvRoutes.visibility = View.GONE
        binding.rvStops.visibility = View.VISIBLE
        binding.btnBack.visibility = View.VISIBLE

        val sortedStops = route.stops.toList().sortedBy { it.second.order }
        
        binding.rvStops.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStops.adapter = object : RecyclerView.Adapter<StopViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stop_selection, parent, false)
                return StopViewHolder(view)
            }

            override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
                val (stopId, stop) = sortedStops[position]
                holder.order.text = stop.order.toString()
                holder.name.text = stop.name
                holder.itemView.setOnClickListener {
                    listener.onRouteSelected(routeId, route.name, stopId, stop.name, stop.order, route.college)
                    dismiss()
                }
            }

            override fun getItemCount() = sortedStops.size
        }
    }

    private fun setupBackAction() {
        binding.btnBack.setOnClickListener {
            binding.tvTitle.text = "Select Your Bus Route"
            binding.tvSubtitle.text = "Find your route to get real-time tracking"
            binding.tilSearch.visibility = View.VISIBLE
            binding.rvRoutes.visibility = View.VISIBLE
            binding.rvStops.visibility = View.GONE
            binding.btnBack.visibility = View.GONE
        }
    }

    class RouteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvRouteName)
        val details: TextView = view.findViewById(R.id.tvRouteDetails)
    }

    class StopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val order: TextView = view.findViewById(R.id.tvStopOrder)
        val name: TextView = view.findViewById(R.id.tvStopName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
