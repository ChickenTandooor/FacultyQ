package com.facultyq.app.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object FacultyQRepository {

    // --------------------------------
    // LOCAL STUDENT STORAGE
    // --------------------------------

    private const val PREFS_NAME = "facultyq_preferences"

    private const val KEY_ENROLLMENT = "student_enrollment"
    private const val KEY_NAME = "student_name"
    private const val KEY_CLASS = "student_class"

    var savedStudent =
        mutableStateOf<Student?>(null)
        private set


    fun initialize(context: Context) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val enrollment =
            preferences.getString(
                KEY_ENROLLMENT,
                null
            )

        val name =
            preferences.getString(
                KEY_NAME,
                null
            )

        val currentClass =
            preferences.getString(
                KEY_CLASS,
                null
            )

        if (
            !enrollment.isNullOrBlank() &&
            !name.isNullOrBlank() &&
            !currentClass.isNullOrBlank()
        ) {

            savedStudent.value =
                Student(
                    enrollmentNumber =
                        enrollment,

                    name =
                        name,

                    currentClass =
                        currentClass
                )
        }
    }


    fun saveStudent(
        context: Context,
        student: Student
    ) {

        val preferences =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        preferences.edit()
            .putString(
                KEY_ENROLLMENT,
                student.enrollmentNumber
            )
            .putString(
                KEY_NAME,
                student.name
            )
            .putString(
                KEY_CLASS,
                student.currentClass
            )
            .apply()

        savedStudent.value = student

        addStudent(student)
    }


    fun clearSavedStudent(
        context: Context
    ) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
            .edit()
            .clear()
            .apply()

        savedStudent.value = null
    }


    // --------------------------------
    // HIGHER AUTHORITY DATA
    // --------------------------------

    val authorities =
        mutableStateListOf(

            Authority(
                id = "A001",
                name = "Dr. Rajesh Sharma",
                role = AuthorityRole.HOD,
                department = "Computer Engineering",
                cabin = "N601",
                status = FacultyStatus.AVAILABLE,
                queueCapacity = 5,
                isAvailableToday = true
            ),

            Authority(
                id = "A002",
                name = "Dr. Anita Deshmukh",
                role = AuthorityRole.DEAN,
                department = "College Administration",
                cabin = "A101",
                status = FacultyStatus.AVAILABLE,
                queueCapacity = 10,
                isAvailableToday = true
            )
        )


    // --------------------------------
    // FACULTY DATA
    // --------------------------------

    val faculties =
        mutableStateListOf(

            Faculty(
                id = "F001",
                name = "Dr. Sharma",
                cabin = "N601",
                status =
                    FacultyStatus.AVAILABLE,
                queueCapacity = 5,
                isAvailableToday = true
            ),

            Faculty(
                id = "F002",
                name = "Dr. Priya",
                cabin = "N602",
                status =
                    FacultyStatus.BUSY,
                queueCapacity = 4,
                isAvailableToday = true
            ),

            Faculty(
                id = "F003",
                name = "Prof. Mehta",
                cabin = "N603",
                status =
                    FacultyStatus.AWAY,
                queueCapacity = 3,
                isAvailableToday = false
            )
        )


    val students =
        mutableStateListOf<Student>()


    val queueEntries =
        mutableStateListOf<QueueEntry>()


    val pendingFaculty =
        mutableStateListOf(
            "Dr. Pending Faculty"
        )


    // --------------------------------
    // STUDENTS
    // --------------------------------

    fun addStudent(
        student: Student
    ) {

        students.removeAll {

            it.enrollmentNumber ==
                    student.enrollmentNumber
        }

        students.add(student)
    }


    fun getStudent(
        enrollmentNumber: String
    ): Student? {

        return students.find {

            it.enrollmentNumber ==
                    enrollmentNumber
        }
    }


    // --------------------------------
    // AUTHORITY SEARCH
    // --------------------------------

    fun searchAuthorities(
        query: String
    ): List<Authority> {

        if (query.isBlank()) {
            return emptyList()
        }

        return authorities.filter {

            it.name.contains(
                query.trim(),
                ignoreCase = true
            )
        }
    }


    fun getAuthority(
        authorityId: String
    ): Authority? {

        return authorities.find {

            it.id == authorityId
        }
    }


    // --------------------------------
    // AUTHORITY MANAGEMENT
    // --------------------------------

    fun addAuthority(
        name: String,
        role: AuthorityRole,
        department: String,
        cabin: String
    ) {

        val newId =
            "A${System.currentTimeMillis()}"

        authorities.add(

            Authority(
                id = newId,
                name = name.trim(),
                role = role,
                department = department.trim(),
                cabin = cabin.trim(),
                status = FacultyStatus.AVAILABLE,
                queueCapacity = 5,
                isAvailableToday = true
            )
        )
    }


    fun updateAuthorityStatus(
        authorityId: String,
        status: FacultyStatus
    ) {

        val index =
            authorities.indexOfFirst {

                it.id == authorityId
            }

        if (index != -1) {

            authorities[index] =
                authorities[index].copy(
                    status = status
                )
        }
    }


    fun updateAuthorityAvailability(
        authorityId: String,
        availableToday: Boolean
    ) {

        val index =
            authorities.indexOfFirst {

                it.id == authorityId
            }

        if (index != -1) {

            authorities[index] =
                authorities[index].copy(
                    isAvailableToday =
                        availableToday
                )
        }
    }


    fun updateAuthorityQueueCapacity(
        authorityId: String,
        capacity: Int
    ) {

        val index =
            authorities.indexOfFirst {

                it.id == authorityId
            }

        if (index != -1) {

            authorities[index] =
                authorities[index].copy(
                    queueCapacity =
                        capacity
                )
        }
    }


    // --------------------------------
    // FACULTY SEARCH
    // --------------------------------

    fun searchFaculty(
        query: String
    ): List<Faculty> {

        if (query.isBlank()) {
            return emptyList()
        }

        return faculties.filter {

            it.name.contains(
                query.trim(),
                ignoreCase = true
            )
        }
    }


    fun getFaculty(
        facultyId: String
    ): Faculty? {

        return faculties.find {

            it.id == facultyId
        }
    }


    // --------------------------------
    // ADD FACULTY
    // ADMIN ONLY
    // --------------------------------

    fun addFaculty(
        name: String,
        cabin: String
    ) {

        val newId =
            "F${System.currentTimeMillis()}"

        faculties.add(

            Faculty(
                id = newId,
                name = name.trim(),
                cabin = cabin.trim(),
                status =
                    FacultyStatus.AVAILABLE,
                queueCapacity = 5,
                isAvailableToday = true
            )
        )
    }


    fun updateFacultyStatus(
        facultyId: String,
        status: FacultyStatus
    ) {

        val index =
            faculties.indexOfFirst {

                it.id == facultyId
            }

        if (index != -1) {

            faculties[index] =
                faculties[index].copy(
                    status = status
                )
        }
    }


    fun updateFacultyAvailability(
        facultyId: String,
        availableToday: Boolean
    ) {

        val index =
            faculties.indexOfFirst {

                it.id == facultyId
            }

        if (index != -1) {

            faculties[index] =
                faculties[index].copy(
                    isAvailableToday =
                        availableToday
                )
        }
    }


    fun updateQueueCapacity(
        facultyId: String,
        capacity: Int
    ) {

        val index =
            faculties.indexOfFirst {

                it.id == facultyId
            }

        if (index != -1) {

            faculties[index] =
                faculties[index].copy(
                    queueCapacity =
                        capacity
                )
        }
    }


    // --------------------------------
    // FACULTY QUEUE
    // --------------------------------

    fun getQueueForFaculty(
        facultyId: String
    ): List<QueueEntry> {

        return queueEntries
            .filter {

                it.targetId == facultyId &&
                        it.targetType ==
                        QueueTargetType.FACULTY &&
                        it.status ==
                        QueueEntryStatus.WAITING
            }
            .sortedBy {

                it.position
            }
    }


    fun getQueueEntryForStudent(
        enrollmentNumber: String,
        facultyId: String
    ): QueueEntry? {

        return queueEntries.find {

            it.studentEnrollmentNumber ==
                    enrollmentNumber &&

                    it.targetId ==
                    facultyId &&

                    it.targetType ==
                    QueueTargetType.FACULTY &&

                    it.status ==
                    QueueEntryStatus.WAITING
        }
    }


    fun joinQueue(
        enrollmentNumber: String,
        facultyId: String,
        purpose: String,
        studentCurrentClass: String
    ): QueueEntry? {

        val faculty =
            getFaculty(facultyId)
                ?: return null

        val currentQueue =
            getQueueForFaculty(
                facultyId
            )

        if (!faculty.isAvailableToday) {
            return null
        }

        if (
            faculty.status ==
            FacultyStatus.DO_NOT_DISTURB ||

            faculty.status ==
            FacultyStatus.AWAY
        ) {
            return null
        }

        if (
            currentQueue.size >=
            faculty.queueCapacity
        ) {
            return null
        }

        val existingEntry =
            getQueueEntryForStudent(
                enrollmentNumber,
                facultyId
            )

        if (existingEntry != null) {
            return existingEntry
        }

        val newEntry =
            QueueEntry(

                id =
                    "Q${System.currentTimeMillis()}",

                studentEnrollmentNumber =
                    enrollmentNumber,

                targetId =
                    facultyId,

                targetType =
                    QueueTargetType.FACULTY,

                purpose =
                    purpose,

                studentCurrentClass =
                    studentCurrentClass,

                position =
                    currentQueue.size + 1
            )

        queueEntries.add(
            newEntry
        )

        return newEntry
    }


    // --------------------------------
    // AUTHORITY QUEUE
    // --------------------------------

    fun getQueueForAuthority(
        authorityId: String
    ): List<QueueEntry> {

        return queueEntries
            .filter {

                it.targetId == authorityId &&

                        it.targetType ==
                        QueueTargetType.AUTHORITY &&

                        it.status ==
                        QueueEntryStatus.WAITING
            }
            .sortedBy {

                it.position
            }
    }


    fun getQueueEntryForStudentAuthority(
        enrollmentNumber: String,
        authorityId: String
    ): QueueEntry? {

        return queueEntries.find {

            it.studentEnrollmentNumber ==
                    enrollmentNumber &&

                    it.targetId ==
                    authorityId &&

                    it.targetType ==
                    QueueTargetType.AUTHORITY &&

                    it.status ==
                    QueueEntryStatus.WAITING
        }
    }


    fun joinAuthorityQueue(
        enrollmentNumber: String,
        authorityId: String,
        purpose: String,
        studentCurrentClass: String
    ): QueueEntry? {

        val authority =
            getAuthority(authorityId)
                ?: return null

        val currentQueue =
            getQueueForAuthority(
                authorityId
            )

        if (!authority.isAvailableToday) {
            return null
        }

        if (
            authority.status ==
            FacultyStatus.DO_NOT_DISTURB ||

            authority.status ==
            FacultyStatus.AWAY
        ) {
            return null
        }

        if (
            currentQueue.size >=
            authority.queueCapacity
        ) {
            return null
        }

        val existingEntry =
            getQueueEntryForStudentAuthority(
                enrollmentNumber,
                authorityId
            )

        if (existingEntry != null) {
            return existingEntry
        }

        val newEntry =
            QueueEntry(

                id =
                    "Q${System.currentTimeMillis()}",

                studentEnrollmentNumber =
                    enrollmentNumber,

                targetId =
                    authorityId,

                targetType =
                    QueueTargetType.AUTHORITY,

                purpose =
                    purpose,

                studentCurrentClass =
                    studentCurrentClass,

                position =
                    currentQueue.size + 1
            )

        queueEntries.add(
            newEntry
        )

        return newEntry
    }


    // --------------------------------
    // LEAVE QUEUE
    // --------------------------------

    fun leaveQueue(
        queueId: String
    ) {

        val index =
            queueEntries.indexOfFirst {

                it.id == queueId
            }

        if (index == -1) {
            return
        }

        val entry =
            queueEntries[index]

        queueEntries[index] =
            entry.copy(

                status =
                    QueueEntryStatus.LEFT
            )

        if (
            entry.targetType ==
            QueueTargetType.FACULTY
        ) {

            reorderFacultyQueue(
                entry.targetId
            )

        } else {

            reorderAuthorityQueue(
                entry.targetId
            )
        }
    }


    // --------------------------------
    // REORDER FACULTY QUEUE
    // --------------------------------

    private fun reorderFacultyQueue(
        facultyId: String
    ) {

        val waitingEntries =
            queueEntries
                .filter {

                    it.targetId ==
                            facultyId &&

                            it.targetType ==
                            QueueTargetType.FACULTY &&

                            it.status ==
                            QueueEntryStatus.WAITING
                }
                .sortedBy {

                    it.position
                }

        waitingEntries.forEachIndexed {

                index,
                entry ->

            val actualIndex =
                queueEntries.indexOfFirst {

                    it.id == entry.id
                }

            if (actualIndex != -1) {

                queueEntries[actualIndex] =
                    entry.copy(

                        position =
                            index + 1
                    )
            }
        }
    }


    // --------------------------------
    // REORDER AUTHORITY QUEUE
    // --------------------------------

    private fun reorderAuthorityQueue(
        authorityId: String
    ) {

        val waitingEntries =
            queueEntries
                .filter {

                    it.targetId ==
                            authorityId &&

                            it.targetType ==
                            QueueTargetType.AUTHORITY &&

                            it.status ==
                            QueueEntryStatus.WAITING
                }
                .sortedBy {

                    it.position
                }

        waitingEntries.forEachIndexed {

                index,
                entry ->

            val actualIndex =
                queueEntries.indexOfFirst {

                    it.id == entry.id
                }

            if (actualIndex != -1) {

                queueEntries[actualIndex] =
                    entry.copy(

                        position =
                            index + 1
                    )
            }
        }
    }


    // --------------------------------
    // FACULTY REGISTRATION
    // --------------------------------

    fun addPendingFaculty(
        name: String
    ) {

        if (
            pendingFaculty.none {

                it.equals(
                    name,
                    ignoreCase = true
                )
            }
        ) {

            pendingFaculty.add(
                name.trim()
            )
        }
    }


    fun verifyFaculty(
        name: String
    ) {

        pendingFaculty.remove(
            name
        )

        if (
            faculties.none {

                it.name.equals(
                    name,
                    ignoreCase = true
                )
            }
        ) {

            faculties.add(

                Faculty(

                    id =
                        "F${System.currentTimeMillis()}",

                    name =
                        name,

                    cabin =
                        "Not assigned",

                    status =
                        FacultyStatus.AWAY,

                    queueCapacity =
                        5,

                    isAvailableToday =
                        false
                )
            )
        }
    }
}