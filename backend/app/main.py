import os
from datetime import datetime

from fastapi import FastAPI
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

SessionLocal = sessionmaker(bind=engine)

Base = declarative_base()


# ---------------------------------------
# FACULTY DATABASE MODEL
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


# ---------------------------------------
# CREATE DATABASE TABLES
# ---------------------------------------

Base.metadata.create_all(bind=engine)


# ---------------------------------------
# FASTAPI APPLICATION
# ---------------------------------------

app = FastAPI(
    title="FacultyQ Backend",
    description="Backend service for FacultyQ campus queue management",
    version="1.0.0"
)


# ---------------------------------------
# REQUEST MODEL
# ---------------------------------------

class FacultyCreate(BaseModel):

    name: str

    cabin: str

    status: str = "AVAILABLE"


# ---------------------------------------
# ROOT ENDPOINT
# ---------------------------------------

@app.get("/")
def root():

    return {
        "application": "FacultyQ",
        "message": "FacultyQ backend is running",
        "containerized": True
    }


# ---------------------------------------
# HEALTH ENDPOINT
# ---------------------------------------

@app.get("/health")
def health():

    return {
        "status": "healthy",
        "service": "facultyq-backend",
        "timestamp": datetime.now().isoformat()
    }


# ---------------------------------------
# GET ALL FACULTY
# ---------------------------------------

@app.get("/faculties")
def get_faculties():

    db = SessionLocal()

    try:

        faculty_list = (
            db.query(Faculty)
            .order_by(Faculty.id)
            .all()
        )

        return {
            "count": len(faculty_list),

            "faculties": [
                {
                    "id": faculty.id,
                    "name": faculty.name,
                    "cabin": faculty.cabin,
                    "status": faculty.status
                }

                for faculty in faculty_list
            ]
        }

    finally:

        db.close()


# ---------------------------------------
# GET SINGLE FACULTY
# ---------------------------------------

@app.get("/faculties/{faculty_id}")
def get_faculty(faculty_id: int):

    db = SessionLocal()

    try:

        faculty = (
            db.query(Faculty)
            .filter(Faculty.id == faculty_id)
            .first()
        )

        if faculty is None:

            return {
                "error": "Faculty not found"
            }

        return {
            "id": faculty.id,
            "name": faculty.name,
            "cabin": faculty.cabin,
            "status": faculty.status
        }

    finally:

        db.close()


# ---------------------------------------
# ADD FACULTY
# ---------------------------------------

@app.post("/faculties")
def add_faculty(
    faculty_data: FacultyCreate
):

    db = SessionLocal()

    try:

        faculty = Faculty(
            name=faculty_data.name,
            cabin=faculty_data.cabin,
            status=faculty_data.status
        )

        db.add(faculty)

        db.commit()

        db.refresh(faculty)

        return {
            "message": "Faculty added successfully",

            "faculty": {
                "id": faculty.id,
                "name": faculty.name,
                "cabin": faculty.cabin,
                "status": faculty.status
            }
        }

    finally:

        db.close()