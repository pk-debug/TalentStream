package com.pawan.hirejetpack.data

import com.pawan.hirejetpack.domain.Job

/**
 * [JobRepository] — abstraction for fetching job listings.
 * Enables seamlessly swapping local mock data with a real Retrofit REST API.
 */
interface JobRepository {
    fun getJobs(): List<Job>
    fun getJobById(id: String): Job?

    companion object : JobRepository {
        private val defaultImpl = DefaultJobRepository()

        override fun getJobs(): List<Job> = defaultImpl.getJobs()
        override fun getJobById(id: String): Job? = defaultImpl.getJobById(id)
    }
}

/**
 * [DefaultJobRepository] — provides job data, structured ready for Retrofit API service.
 */
class DefaultJobRepository : JobRepository {

    private val sampleJobs = listOf(
        Job("1", "Android Engineer", "Zenith Labs", "Bengaluru", "₹18–28 LPA", listOf("Kotlin", "Compose")),
        Job("2", "Staff Mobile Engineer", "Northwind", "Remote", "₹40–55 LPA", listOf("KMP", "Architecture")),
        Job("3", "Kotlin Multiplatform Dev", "OrbitPay", "Hyderabad", "₹22–32 LPA", listOf("KMP", "iOS")),
        Job("4", "Senior Android Developer", "Trailhead", "Pune", "₹25–35 LPA", listOf("Coroutines", "Hilt")),
        Job("5", "Android Team Lead", "Vertex", "Remote", "₹45–60 LPA", listOf("Leadership", "Compose")),
    )

    override fun getJobs(): List<Job> = sampleJobs

    override fun getJobById(id: String): Job? = sampleJobs.find { it.id == id }
}
