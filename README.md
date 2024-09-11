# DemoViewer

## Overview

`DemoViewer` is a Java application that demonstrates basic 3D graphics rendering using Java Swing. It features a graphical user interface (GUI) with sliders to control the 3D rotation of a colored tetrahedron model. The application showcases fundamental concepts in 3D graphics, including transformations, projection, and depth buffering.

## Features

- **3D Rotation Controls**: Adjust the horizontal (heading), vertical (pitch), and tilt (roll) rotations of the 3D model using sliders.
- **Depth Buffering**: Implements a z-buffer to handle depth sorting and rendering of the model's surfaces.
- **Basic 3D Graphics**: Renders a colored tetrahedron using basic 3D transformation matrices.

## How to Run

1. Ensure you have Java installed on your system.
2. Save the code to a file named `DemoViewer.java`.
3. Compile the code using `javac DemoViewer.java`.
4. Run the application using `java DemoViewer`.

## Code Structure

- `DemoViewer`: The main class that sets up the JFrame, sliders, and rendering panel. It handles the 3D transformations and rendering of the model.
- `Vertex`: Represents a 3D point with x, y, and z coordinates.
- `Triangle`: Represents a 3D triangle with three vertices and a color.
- `Matrix3`: Provides basic 3x3 matrix operations for 3D transformations.

## Dependencies

- Java Standard Library
