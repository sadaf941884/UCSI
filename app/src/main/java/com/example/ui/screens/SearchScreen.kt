package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.data.model.InternshipEntity
import com.example.ui.theme.*

@Composable
fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    courses: List<CourseEntity>,
    internships: List<InternshipEntity>,
    onCourseClick: (CourseEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    val query = searchQuery.trim()

    val matchedCourses = remember(query, courses) {
        if (query.isBlank()) emptyList()
        else courses.filter {
            it.courseCode.contains(query, ignoreCase = true) ||
            it.courseTitle.contains(query, ignoreCase = true) ||
            it.facultyName.contains(query, ignoreCase = true) ||
            it.room.contains(query, ignoreCase = true)
        }
    }

    val matchedInternships = remember(query, internships) {
        if (query.isBlank()) emptyList()
        else internships.filter {
            it.companyName.contains(query, ignoreCase = true) ||
            it.roleTitle.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true)
        }
    }

    val matchedRooms = remember(query) {
        if (query.isBlank()) emptyList()
        else BananiCampusFloors.flatMap { floor ->
            floor.rooms.filter { it.contains(query, ignoreCase = true) }.map { room ->
                Pair(room, "Floor ${floor.levelNumber} (${floor.name})")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Search Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search courses, rooms, faculty, jobs...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("global_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (query.isBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Search the entire UCSI Bangladesh Campus", fontWeight = FontWeight.Bold)
                        Text("Find lecture rooms, course syllabi, faculty offices, and jobs.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                // Rooms
                if (matchedRooms.isNotEmpty()) {
                    item { Text("Campus Rooms & Facilities (${matchedRooms.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = UcsiCrimson) }
                    items(matchedRooms) { (room, floorDesc) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = UcsiNavy)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(room, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(floorDesc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                // Courses
                if (matchedCourses.isNotEmpty()) {
                    item { Text("Academic Courses (${matchedCourses.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = UcsiCrimson) }
                    items(matchedCourses, key = { it.id }) { course ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCourseClick(course) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("${course.courseCode} • ${course.courseTitle}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Faculty: ${course.facultyName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Room: ${course.room} (Floor ${course.floor}) • Attendance: ${course.attendancePercent}%", fontSize = 11.sp, color = UcsiNavy, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Internships
                if (matchedInternships.isNotEmpty()) {
                    item { Text("Co-Op Internships (${matchedInternships.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = UcsiCrimson) }
                    items(matchedInternships, key = { it.id }) { job ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("${job.roleTitle} @ ${job.companyName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${job.category} • Stipend: ${job.stipend}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                if (matchedRooms.isEmpty() && matchedCourses.isEmpty() && matchedInternships.isEmpty()) {
                    item {
                        Text("No matching campus records found for \"$query\".", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
