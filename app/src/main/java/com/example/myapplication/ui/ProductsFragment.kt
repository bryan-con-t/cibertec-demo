package com.example.myapplication.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapters.ProductAdapter
import com.example.myapplication.entity.Product
import com.example.myapplication.data.api.FakeStoreApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsFragment : Fragment(R.layout.fragment_products) {

    private lateinit var rvProductos: RecyclerView
    private val productos = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvProductos = view.findViewById(R.id.recyclerViewProductos)
        rvProductos.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductAdapter(productos)
        rvProductos.adapter = adapter

        cargarProductosDesdeApi()
    }

    private fun cargarProductosDesdeApi() {
        FakeStoreApiClient.apiService.getProducts().enqueue(object :
            Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful && response.body() != null) {
                    productos.clear()
                    productos.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(requireContext(),
                    "Error al cargar: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
