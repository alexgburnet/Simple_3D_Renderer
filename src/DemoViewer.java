import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class DemoViewer {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        //Slider to control horizontal rotation
        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        // slider to control vertical rotation
        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);


        // panel to display render results
        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Triangles
                ArrayList<Triangle> tris = new ArrayList<>();
                tris.add(new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(-100, 100, -100),
                        Color.WHITE));
                tris.add(new Triangle(new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(100, -100, -100),
                        Color.RED));
                tris.add(new Triangle(new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(100, 100, 100),
                        Color.GREEN));
                tris.add(new Triangle(new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(-100, -100, 100),
                        Color.BLUE));

                //g2.translate(getWidth() / 2, getHeight() / 2);
                g2.setColor(Color.WHITE);

                double headerAngle = Math.toRadians(headingSlider.getValue());

                Matrix3 headingTransform = new Matrix3(new double[] {
                        Math.cos(headerAngle), 0, -Math.sin(headerAngle),
                        0, 1, 0,
                        Math.sin(headerAngle), 0, Math.cos(headerAngle)
                });

                double pitchAngle = Math.toRadians(pitchSlider.getValue());

                Matrix3 pitchTransform = new Matrix3(new double[] {
                        1, 0, 0,
                        0, Math.cos(pitchAngle), Math.sin(pitchAngle),
                        0, -Math.sin(pitchAngle), Math.cos(pitchAngle)
                });

                Matrix3 transform = headingTransform.multiply(pitchTransform);

                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                double[] zBuffer = new double[img.getWidth() * img.getHeight()];
                Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

                for (Triangle t : tris) {

                    Vertex v1 = transform.transform(t.v1);
                    Vertex v2 = transform.transform(t.v2);
                    Vertex v3 = transform.transform(t.v3);

                    v1.x = v1.x + getWidth() / 2;
                    v1.y = v1.y + getHeight() / 2;
                    v2.x = v2.x + getWidth() / 2;
                    v2.y = v2.y + getHeight() / 2;
                    v3.x = v3.x + getWidth() / 2;
                    v3.y = v3.y + getHeight() / 2;

                    int minX = (int) Math.min(v1.x, Math.min(v2.x, v3.x));
                    int maxX = (int) Math.max(v1.x, Math.max(v2.x, v3.x));
                    int minY = (int) Math.min(v1.y, Math.min(v2.y, v3.y));
                    int maxY = (int) Math.max(v1.y, Math.max(v2.y, v3.y));

                    double triangleArea = (v1.x - v3.x) * (v2.y - v1.y) - (v1.x - v2.x) * (v3.y - v1.y);

                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
                    Vertex norm = new Vertex(
                            ab.y * ac.z - ab.z * ac.y,
                            ab.z * ac.x - ab.x * ac.z,
                            ab.x * ac.y - ab.y * ac.x
                    );


                    double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);

                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;

                    double angleCos = Math.abs(norm.z);

                    for (int y = minY; y < maxY; y++) {
                        for (int x = minX; x < maxX; x++) {
                            double b1 = ((x - v3.x) * (v2.y - v3.y) - (v2.x - v3.x) * (y - v3.y)) / triangleArea;
                            double b2 = ((x - v1.x) * (v3.y - v1.y) - (v3.x - v1.x) * (y - v1.y)) / triangleArea;
                            double b3 = ((x - v2.x) * (v1.y - v2.y) - (v1.x - v2.x) * (y - v2.y)) / triangleArea;

                            double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                            int zIndex = y * img.getWidth() + x;

                            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                if (zBuffer[zIndex] < depth) {
                                    //img.setRGB(x, y, t.color.getRGB());
                                    img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
                                    zBuffer[zIndex] = depth;
                                }

                            }
                        }
                    }

                    g2.drawImage(img, 0, 0, null);

                    /*
                    Path2D path = new Path2D.Double();
                    path.moveTo(v1.x, v1.y);
                    path.lineTo(v2.x, v2.y);
                    path.lineTo(v3.x, v3.y);
                    path.closePath();
                    g2.draw(path);
                    */

                }
            }
        };

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());

        pane.add(renderPanel);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);

        return new Color(red, green, blue);
    }
}

class Vertex {
    double x;
    double y;
    double z;
    Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

class Triangle {
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Color color;
    Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }
}

class Matrix3 {
    double[] values;
    Matrix3(double[] values) {
        this.values = values;
    }

    Matrix3 multiply(Matrix3 other) {
        double[] result = new double[9];
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 3; row++) {
                result[col * 3 + row] = values[row] * other.values[col * 3] +
                        values[row + 3] * other.values[col * 3 + 1] +
                        values[row + 6] * other.values[col * 3 + 2];
            }
        }

        return new Matrix3(result);
    }

    Vertex transform(Vertex in) {
        return new Vertex(
                in.x * values[0] + in.y * values[3] + in.z * values[6],
                in.x * values[1] + in.y * values[4] + in.z * values[7],
                in.x * values[2] + in.y * values[5] + in.z * values[8]
        );
    }
}