import os
from datetime import datetime

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

from sqlalchemy import Column, Integer, String, create_engine
from sqlalchemy.orm import declarative_base, sessionmaker


# ---------------------------------------
# DATABASE CONFIGURATION
# ---------------------------------------

DATABASE_URL = os.getenv(
    "DATABASE_URL",
    "postgresql+psycopg://facultyq:facultyq_password@database:5432/facultyq"
)

engine = create_engine(DATABASE_URL)

SessionLocal = sessionmaker(
    bind=engine,
    autocommit=False,
    autoflush=False
)

Base = declarative_base()


# ---------------------------------------
# DATABASE MODELS
# ---------------------------------------

class Faculty(Base):

    __tablename__ = "faculties"

    id = Column(
        Integer,
        primary_key=True,
        index=True
    )

    name = Column(
        String,
        nullable=False
    )

    cabin = Column(
        String,
        nullable=False
    )

    status = Column(
        String,
        default="AVAILABLE"
    )


class Authority(Base):

    __tablename__ = "authorities"

    id = Column(
        String,
        primary_key=True,
        index=True
    )

    name = Column(
        String,
        nullable=False
    )

    role = Column(
        String,
        nullable=False
    )

    department = Column(
        String,
        nullable=False
    )

    cabin = Column(
        String,
        nullable=False
    )

    status = Column(
        String,
        default="AVAILABLE"
    )

    queue_capacity = Column(
        Integer,
        default=5
    )

    is_available_today = Column(
        Integer,
        default=1
    )


class Student(Base):

    __tablename__ = "students"

    enrollment_number = Column(
        String,
        primary_key=True,
        index=True
    )

    name = Column(
        String,
        nullable=False
    )

    current_class = Column(
        String,
        nullable=False
    )


class QueueEntry(Base):

    __tablename__ = "queue_entries"

    id = Column(
        String,
        primary_key=True,
        index=True
    )

    student_enrollment_number = Column(
        String,
        nullable=False
    )

    target_id = Column(
        String,
        nullable=False
    )

    target_type = Column(
        String,
        nullable=False
    )

    purpose = Column(
        String,
        nullable=False
    )

    student_current_class = Column(
        String,
        nullable=False
    )

    position = Column(
        Integer,
        nullable=False
    )

    status = Column(
        String,
        default="WAITING"
    )


# ---------------------------------------
# CREATE DATABASE TABLES
# ---------------------------------------

Base.metadata.create_all(
    bind=engine
)


# ---------------------------------------
# FASTAPI APPLICATION
# ---------------------------------------

app = FastAPI(
    title="FacultyQ Backend",
    description="Backend service for FacultyQ campus queue management",
    version="2.0.0"
)


# ---------------------------------------
# REQUEST MODELS
# ---------------------------------------

class FacultyCreate(BaseModel):

    name: str
    cabin: str
    status: str = "AVAILABLE"


class AuthorityCreate(BaseModel):

    id: str
    name: str
    role: str
    department: str
    cabin: str
    status: str = "AVAILABLE"
    queue_capacity: int = 5
    is_available_today: bool = True


class AuthorityStatusUpdate(BaseModel):

    status: str


class AuthorityAvailabilityUpdate(BaseModel):

    is_available_today: bool


class StudentCreate(BaseModel):

    enrollment_number: str
    name: str
    current_class: str


class QueueCreate(BaseModel):

    student_enrollment_number: str
    purpose: str


# ---------------------------------------
# HELPER FUNCTIONS
# ---------------------------------------

def authority_to_dict(authority):

    return {
        "id": authority.id,
        "name": authority.name,
        "role": authority.role,
        "department": authority.department,
        "cabin": authority.cabin,
        "status": authority.status,
        "queue_capacity": authority.queue_capacity,
        "is_available_today": bool(
            authority.is_available_today
        )
    }


def queue_entry_to_dict(entry):

    return {
        "id": entry.id,
        "student_enrollment_number":
            entry.student_enrollment_number,
        "target_id":
            entry.target_id,
        "target_type":
            entry.target_type,
        "purpose":
            entry.purpose,
        "student_current_class":
            entry.student_current_class,
        "position":
            entry.position,
        "status":
            entry.status
    }


# ---------------------------------------
# ROOT
# ---------------------------------------

@app.get("/")
def root():

    return {
        "application": "FacultyQ",
        "message": "FacultyQ backend is running",
        "version": "2.0.0",
        "containerized": True
    }


# ---------------------------------------
# HEALTH
# ---------------------------------------

@app.get("/health")
def health():

    return {
        "status": "healthy",
        "service": "facultyq-backend",
        "timestamp": datetime.now().isoformat()
    }


# =======================================
# AUTHORITY ENDPOINTS
# =======================================

# ---------------------------------------
# GET ALL AUTHORITIES
# ---------------------------------------

@app.get("/authorities")
def get_authorities():

    db = SessionLocal()

    try:

        authority_list = (
            db.query(Authority)
            .order_by(Authority.id)
            .all()
        )

        return {
            "count": len(authority_list),
            "authorities": [
                authority_to_dict(authority)
                for authority in authority_list
            ]
        }

    finally:

        db.close()


# ---------------------------------------
# GET SINGLE AUTHORITY
# ---------------------------------------

@app.get("/authorities/{authority_id}")
def get_authority(
    authority_id: str
):

    db = SessionLocal()

    try:

        authority = (
            db.query(Authority)
            .filter(
                Authority.id == authority_id
            )
            .first()
        )

        if authority is None:

            raise HTTPException(
                status_code=404,
                detail="Authority not found"
            )

        return authority_to_dict(
            authority
        )

    finally:

        db.close()


# ---------------------------------------
# ADD AUTHORITY
# ---------------------------------------

@app.post("/authorities")
def add_authority(
    authority_data: AuthorityCreate
):

    db = SessionLocal()

    try:

        existing = (
            db.query(Authority)
            .filter(
                Authority.id ==
                authority_data.id
            )
            .first()
        )

        if existing is not None:

            raise HTTPException(
                status_code=409,
                detail="Authority already exists"
            )

        authority = Authority(
            id=authority_data.id,
            name=authority_data.name,
            role=authority_data.role,
            department=authority_data.department,
            cabin=authority_data.cabin,
            status=authority_data.status,
            queue_capacity=authority_data.queue_capacity,
            is_available_today=
                1 if authority_data.is_available_today
                else 0
        )

        db.add(authority)
        db.commit()
        db.refresh(authority)

        return {
            "message": "Authority added successfully",
            "authority":
                authority_to_dict(authority)
        }

    finally:

        db.close()


# ---------------------------------------
# UPDATE AUTHORITY STATUS
# ---------------------------------------

@app.patch("/authorities/{authority_id}/status")
def update_authority_status(
    authority_id: str,
    status_data: AuthorityStatusUpdate
):

    db = SessionLocal()

    try:

        authority = (
            db.query(Authority)
            .filter(
                Authority.id == authority_id
            )
            .first()
        )

        if authority is None:

            raise HTTPException(
                status_code=404,
                detail="Authority not found"
            )

        authority.status = status_data.status

        db.commit()
        db.refresh(authority)

        return authority_to_dict(
            authority
        )

    finally:

        db.close()


# ---------------------------------------
# UPDATE AUTHORITY AVAILABILITY
# ---------------------------------------

@app.patch("/authorities/{authority_id}/availability")
def update_authority_availability(
    authority_id: str,
    availability_data:
        AuthorityAvailabilityUpdate
):

    db = SessionLocal()

    try:

        authority = (
            db.query(Authority)
            .filter(
                Authority.id == authority_id
            )
            .first()
        )

        if authority is None:

            raise HTTPException(
                status_code=404,
                detail="Authority not found"
            )

        authority.is_available_today = (
            1
            if availability_data.is_available_today
            else 0
        )

        db.commit()
        db.refresh(authority)

        return authority_to_dict(
            authority
        )

    finally:

        db.close()


# =======================================
# STUDENT ENDPOINTS
# =======================================

# ---------------------------------------
# ADD / REGISTER STUDENT
# ---------------------------------------

@app.post("/students")
def add_student(
    student_data: StudentCreate
):

    db = SessionLocal()

    try:

        existing = (
            db.query(Student)
            .filter(
                Student.enrollment_number ==
                student_data.enrollment_number
            )
            .first()
        )

        if existing is not None:

            existing.name = student_data.name
            existing.current_class = (
                student_data.current_class
            )

            db.commit()
            db.refresh(existing)

            student = existing

        else:

            student = Student(
                enrollment_number=
                    student_data.enrollment_number,
                name=student_data.name,
                current_class=
                    student_data.current_class
            )

            db.add(student)
            db.commit()
            db.refresh(student)

        return {
            "message": "Student saved successfully",
            "student": {
                "enrollment_number":
                    student.enrollment_number,
                "name":
                    student.name,
                "current_class":
                    student.current_class
            }
        }

    finally:

        db.close()


# ---------------------------------------
# GET STUDENT
# ---------------------------------------

@app.get("/students/{enrollment_number}")
def get_student(
    enrollment_number: str
):

    db = SessionLocal()

    try:

        student = (
            db.query(Student)
            .filter(
                Student.enrollment_number ==
                enrollment_number
            )
            .first()
        )

        if student is None:

            raise HTTPException(
                status_code=404,
                detail="Student not found"
            )

        return {
            "enrollment_number":
                student.enrollment_number,
            "name":
                student.name,
            "current_class":
                student.current_class
        }

    finally:

        db.close()


# =======================================
# AUTHORITY QUEUE ENDPOINTS
# =======================================

# ---------------------------------------
# GET AUTHORITY QUEUE
# ---------------------------------------

@app.get("/authorities/{authority_id}/queue")
def get_authority_queue(
    authority_id: str
):

    db = SessionLocal()

    try:

        authority = (
            db.query(Authority)
            .filter(
                Authority.id == authority_id
            )
            .first()
        )

        if authority is None:

            raise HTTPException(
                status_code=404,
                detail="Authority not found"
            )

        queue = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.target_id ==
                    authority_id,
                QueueEntry.target_type ==
                    "AUTHORITY",
                QueueEntry.status.in_(
                    ["WAITING", "SERVING"]
                )
            )
            .order_by(
                QueueEntry.position
            )
            .all()
        )

        return {
            "authority_id": authority_id,
            "count": len(queue),
            "queue": [
                queue_entry_to_dict(entry)
                for entry in queue
            ]
        }

    finally:

        db.close()


# ---------------------------------------
# JOIN AUTHORITY QUEUE
# ---------------------------------------

@app.post("/authorities/{authority_id}/queue")
def join_authority_queue(
    authority_id: str,
    queue_data: QueueCreate
):

    db = SessionLocal()

    try:

        authority = (
            db.query(Authority)
            .filter(
                Authority.id == authority_id
            )
            .first()
        )

        if authority is None:

            raise HTTPException(
                status_code=404,
                detail="Authority not found"
            )

        student = (
            db.query(Student)
            .filter(
                Student.enrollment_number ==
                queue_data.student_enrollment_number
            )
            .first()
        )

        if student is None:

            raise HTTPException(
                status_code=404,
                detail="Student not found"
            )

        if not bool(
            authority.is_available_today
        ):

            raise HTTPException(
                status_code=400,
                detail="Authority is not available today"
            )

        if authority.status in [
            "AWAY",
            "DO_NOT_DISTURB"
        ]:

            raise HTTPException(
                status_code=400,
                detail="Authority is not accepting new queue entries"
            )

        current_queue = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.target_id ==
                    authority_id,
                QueueEntry.target_type ==
                    "AUTHORITY",
                QueueEntry.status.in_(
                    ["WAITING", "SERVING"]
                )
            )
            .order_by(
                QueueEntry.position
            )
            .all()
        )

        if len(current_queue) >= authority.queue_capacity:

            raise HTTPException(
                status_code=400,
                detail="Authority queue is full"
            )

        already_joined = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.student_enrollment_number ==
                    student.enrollment_number,
                QueueEntry.target_id ==
                    authority_id,
                QueueEntry.target_type ==
                    "AUTHORITY",
                QueueEntry.status.in_(
                    ["WAITING", "SERVING"]
                )
            )
            .first()
        )

        if already_joined is not None:

            raise HTTPException(
                status_code=409,
                detail="Student is already in this queue"
            )

        new_position = len(current_queue) + 1

        entry = QueueEntry(
            id=f"AQ{int(datetime.now().timestamp() * 1000)}",
            student_enrollment_number=
                student.enrollment_number,
            target_id=authority_id,
            target_type="AUTHORITY",
            purpose=queue_data.purpose,
            student_current_class=
                student.current_class,
            position=new_position,
            status="WAITING"
        )

        db.add(entry)
        db.commit()
        db.refresh(entry)

        return {
            "message": "Joined authority queue successfully",
            "queue_entry":
                queue_entry_to_dict(entry)
        }

    finally:

        db.close()


# ---------------------------------------
# SERVE QUEUE ENTRY
# ---------------------------------------

@app.post("/queue/{queue_id}/serve")
def serve_queue_entry(
    queue_id: str
):

    db = SessionLocal()

    try:

        entry = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.id == queue_id
            )
            .first()
        )

        if entry is None:

            raise HTTPException(
                status_code=404,
                detail="Queue entry not found"
            )

        if entry.status != "WAITING":

            raise HTTPException(
                status_code=400,
                detail="Only waiting students can be served"
            )

        first_waiting = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.target_id ==
                    entry.target_id,
                QueueEntry.target_type ==
                    entry.target_type,
                QueueEntry.status ==
                    "WAITING"
            )
            .order_by(
                QueueEntry.position
            )
            .first()
        )

        if first_waiting is None or \
           first_waiting.id != entry.id:

            raise HTTPException(
                status_code=400,
                detail="Only the first waiting student can be served"
            )

        entry.status = "SERVING"

        db.commit()
        db.refresh(entry)

        return {
            "message": "Queue entry is now being served",
            "queue_entry":
                queue_entry_to_dict(entry)
        }

    finally:

        db.close()


# ---------------------------------------
# COMPLETE QUEUE ENTRY
# ---------------------------------------

@app.post("/queue/{queue_id}/complete")
def complete_queue_entry(
    queue_id: str
):

    db = SessionLocal()

    try:

        entry = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.id == queue_id
            )
            .first()
        )

        if entry is None:

            raise HTTPException(
                status_code=404,
                detail="Queue entry not found"
            )

        if entry.status != "SERVING":

            raise HTTPException(
                status_code=400,
                detail="Only serving students can be completed"
            )

        entry.status = "COMPLETED"

        remaining = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.target_id ==
                    entry.target_id,
                QueueEntry.target_type ==
                    entry.target_type,
                QueueEntry.status ==
                    "WAITING"
            )
            .order_by(
                QueueEntry.position
            )
            .all()
        )

        for new_position, waiting_entry in enumerate(
            remaining,
            start=1
        ):

            waiting_entry.position = new_position

        db.commit()

        return {
            "message": "Queue entry completed",
            "queue_entry":
                queue_entry_to_dict(entry)
        }

    finally:

        db.close()


# ---------------------------------------
# LEAVE QUEUE
# ---------------------------------------

@app.post("/queue/{queue_id}/leave")
def leave_queue(
    queue_id: str
):

    db = SessionLocal()

    try:

        entry = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.id == queue_id
            )
            .first()
        )

        if entry is None:

            raise HTTPException(
                status_code=404,
                detail="Queue entry not found"
            )

        if entry.status not in [
            "WAITING",
            "SERVING"
        ]:

            raise HTTPException(
                status_code=400,
                detail="Queue entry cannot be left"
            )

        target_id = entry.target_id
        target_type = entry.target_type

        entry.status = "LEFT"

        remaining = (
            db.query(QueueEntry)
            .filter(
                QueueEntry.target_id ==
                    target_id,
                QueueEntry.target_type ==
                    target_type,
                QueueEntry.status ==
                    "WAITING"
            )
            .order_by(
                QueueEntry.position
            )
            .all()
        )

        for new_position, waiting_entry in enumerate(
            remaining,
            start=1
        ):

            waiting_entry.position = new_position

        db.commit()

        return {
            "message": "Queue entry left successfully",
            "queue_id": queue_id
        }

    finally:

        db.close()