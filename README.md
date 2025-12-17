# Student Grade Management System

A Java-based console application for managing students and their grades with support for different student types and subject categories.

## Features

- **Student Management**: Add and view both Regular and Honors students
- **Grade Recording**: Record grades for various subjects (Core and Elective)
- **Grade Reports**: View individual student grade reports with averages
- **Automatic Grading**: Different grading scales for Regular and Honors students
- **Pre-loaded Data**: Comes with 5 sample students for testing
- **Export Grade Report - Save student reports to text files
- **Calculate GPA - Convert percentage grades to 4.0 GPA scale
- **﻿﻿Bulk Grade Import - Load multiple grades from CSV file
- **Grade Statistics - View class-wide analytics (highest, lowest, median, standard deviation)
- ﻿﻿**Student Search - Find studentsby name or ID with partial matching


## System Architecture

### Class Structure

**Interfaces:**
- `Gradable` - Defines grading behavior for students

**Abstract Classes:**
- `Student` - Base class for all student types
- `Subject` - Base class for all subject types

**Concrete Classes:**
- `RegularStudent` - Standard students with 50% passing threshold
- `HonorsStudent` - Advanced students with 60% passing threshold
- `CoreSubject` - Required subjects (Math, English, Science)
- `ElectiveSubject` - Optional subjects (Music, Art, PE)
- `Grade` - Stores individual grade records
- `StudentManager` - Handles student operations
- `GradeManager` - Handles grade operations

## Grading Systems

### Regular Students
- **Passing Grade**: 50 or above
- **Grade Scale**:
  - A: 90-100
  - B: 80-89
  - C: 70-79
  - D: 60-69
  - E: 50-59
  - F: Below 50

### Honors Students
- **Passing Grade**: 60 or above
- **Grade Scale**:
  - A+: 90-100
  - A: 80-89
  - B: 70-79
  - C: 60-69
  - F: Below 60

## How to Use

### Compilation
```bash
javac Main.java
```

### Running
```bash
java Main
```

### Menu Options

1. **View Students** - Display all registered students with their details
2. **Add Student** - Register a new Regular or Honors student
3. **Record Grade** - Enter a grade for a specific student and subject
4. **View Grade Report** - See all grades and average for a student
5. **Exit** - Close the application

### Sample Workflow

1. Start the program
2. Select option 1 to view the 5 pre-loaded students (IDs 1-5)
3. Select option 3 to record a grade:
   - Enter student ID (e.g., 1)
   - Choose subject type (Core or Elective)
   - Select specific subject
   - Enter score (0-100)
4. Select option 4 to view the grade report for that student

## Pre-loaded Students

The system comes with 5 sample students:
- Alice (ID: 1) - Regular Student, Age 18
- Bob (ID: 2) - Regular Student, Age 17
- Charlie (ID: 3) - Regular Student, Age 19
- Diana (ID: 4) - Honors Student, Age 18
- Edward (ID: 5) - Honors Student, Age 17

## Available Subjects

**Core Subjects:**
- Math (MATH101)
- English (ENG101)
- Science (SCI101)

**Elective Subjects:**
- Music (MUS101)
- Art (ART101)
- PE (PE101)

## Technical Details

- **Language**: Java
- **Storage**: In-memory arrays (no database)
- **Capacity**: Up to 50 students and 100 grade records
- **ID Generation**: Automatic incremental IDs for students and grades

## Requirements

- Java Development Kit (JDK) 8 or higher
- Command-line interface or terminal

## Limitations

- Data is not persisted between sessions
- Fixed array sizes (50 students, 100 grades)
- Console-based interface only
- No data validation for edge cases (e.g., negative ages, scores above 100)

## Future Enhancements

- Database integration for persistent storage
- Input validation and error handling
- Support for more subject types
- Grade history and trend analysis
- Export reports to files
- Graphical user interface (GUI)
