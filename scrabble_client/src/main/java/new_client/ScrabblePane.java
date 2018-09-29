package new_client;

import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// TODO: Credit to: https://stackoverflow.com/a/31761362 for the code
public class ScrabblePane extends Pane {
    private final ScrabbleCanvas canvas;
    private final TextField letterType;
    private Point letterType_cell;

    public ScrabblePane() {
        super();

        canvas = new ScrabbleCanvas(getWidth(), getHeight());
        letterType = new TextField();

        letterType.setVisible(false);
        letterType.setAlignment(Pos.CENTER);

        // only allow one letter
        letterType.setTextFormatter(
                new TextFormatter<String>((TextFormatter.Change change) -> {
            String newText = change.getControlNewText();
            System.out.println(change + "\t\t" + newText);

            if (newText.length() > 1)
                return null;
            else if (change.getControlText().length() == 0 &&
                    change.isAdded() && Character.isLetter(newText.charAt(0))) {
                change.setText(newText.toUpperCase());
            }
            System.out.println("WE END HERE");
            return change;
        }));

        letterType.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                            canvas.setLetter(
                                    canvas.getSelectedCell().x,
                                    canvas.getSelectedCell().y,
                                    letterType.getText().isEmpty() ? null : letterType.getText().charAt(0));
                        letterType.setVisible(false);
                        canvas.requestFocus();
                        break;
                    case ESCAPE:
                        letterType.setVisible(false);
                        canvas.requestFocus();
                        break;
                    default:
                        break;
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            System.out.println("GOT CLICKED");
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                openTextField();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (letterType.isVisible()) {
                    canvas.setLetter(
                            letterType_cell.x,
                            letterType_cell.y,
                            letterType.getText().isEmpty() ? null : letterType.getText().charAt(0));
                    canvas.requestFocus();
                }

                letterType.setVisible(false);
            }
        });

        canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:
                    case RIGHT:
                    case UP:
                    case DOWN:
                        break;
                    default:
                        if (event.getText().length() == 1 &&
                                Character.isLetter(event.getText().charAt(0)) ||
                                event.getCode() == KeyCode.ENTER)
                            openTextField();
                        break;
                }

            }
        });

        canvas.widthProperty().addListener(o -> {
            // TODO: Why do I have to put this together and not in canvas?
            canvas.repaint();
            positionTextField();
            letterType.autosize();
        });
        canvas.heightProperty().addListener(o -> {
            canvas.repaint();
            positionTextField();
            letterType.autosize();
        } );

        getChildren().add(canvas);
        getChildren().add(letterType);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    private void positionTextField() {
        if (canvas.getSelectedCell() == null)
            return;

        System.out.println("NEW SIZE: " + getWidth() + " " + getHeight());
        Point2D selected_xy = canvas.toCell(canvas.getSelectedCell());
        Dimension2D cell_size = canvas.getCellSize();
        // move textbox to cell
        letterType.setLayoutX(selected_xy.getX());
        letterType.setLayoutY(selected_xy.getY());

        letterType.setPrefSize(cell_size.getWidth(), cell_size.getHeight());

        letterType.setPadding(new Insets(-0.05 * cell_size.getHeight(), 0,0, 0));

        Font f = canvas.getLetterFont();
        letterType.setFont(new Font(f.getName(), f.getSize()/1.2));
    }

    private void openTextField() {
        if (canvas.getSelectedCell() == null)
            return;

        positionTextField();

        letterType_cell = canvas.getSelectedCell();
        Character letter = canvas.getLetter(canvas.getSelectedCell().x, canvas.getSelectedCell().y);
        if (letter != null)
            letterType.setText(letter.toString());
        else
            letterType.setText("");


        letterType.setVisible(true);
        letterType.requestFocus();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        final double x = snappedLeftInset();
        final double y = snappedTopInset();
        // Java 9 - snapSize is depricated used snapSizeX() and snapSizeY() accordingly
        final double w = snapSize(getWidth()) - x - snappedRightInset();
        final double h = snapSize(getHeight()) - y - snappedBottomInset();
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        canvas.setWidth(w);
        canvas.setHeight(h);
    }


    private class ScrabbleCanvas extends Canvas {
        private int num_rows = 20;
        private int num_cols = 20;

        private Map<Point, Character> letters = new HashMap<>();

        private Dimension2D cell_size;
        private Color select_color = Color.LIGHTBLUE;
        private Point selected_cell;
        private double line_width = 1.2;

        public ScrabbleCanvas(double width, double height) {
            super(width, height);
            deepPaint();

            this.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                shallowPaint(getCellHovering(e));
                requestFocus();
            });

            this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                Point new_selected_cell = null;

                switch (e.getCode()) {
                    case LEFT:
                        if (selected_cell.getX() > 0)
                            new_selected_cell = new Point(selected_cell.x - 1, selected_cell.y);
                        break;
                    case RIGHT:
                        if (selected_cell.getX() < num_cols - 1)
                            new_selected_cell = new Point(selected_cell.x + 1, selected_cell.y);
                        break;
                    case DOWN:
                        if (selected_cell.getY() < num_rows - 1)
                            new_selected_cell = new Point(selected_cell.x, selected_cell.y + 1);
                        break;
                    case UP:
                        if (selected_cell.getY() > 0)
                            new_selected_cell = new Point(selected_cell.x, selected_cell.y - 1);
                        break;
                    default:
                        return;
                }

                if (new_selected_cell != null) {
                    shallowPaint(new_selected_cell);
                }

                requestFocus();
            });
        }

        public Dimension2D getCellSize() {
            return cell_size;
        }

        public Character getLetter(int row, int col) {
            return letters.get(new Point(row, col));
        }

        public void setLetter(int row, int col, Character c) {
            Point cell = new Point(row, col);

            if (c == null) {
                letters.remove(cell);
            } else {
                letters.put(cell, c);
            }

            refreshCell(getGraphicsContext2D(), cell);
        }

        private Point getCellHovering(MouseEvent e) {
            int row =(int)(e.getX()/cell_size.getWidth());
            int col = (int)(e.getY()/cell_size.getHeight());

            return new Point(row, col);
        }

        public Point2D toCell(Point coordinate) {
            double x = coordinate.getX() * cell_size.getWidth();
            double y = coordinate.getY() * cell_size.getHeight();

            return new Point2D(x, y);
        }

        public Point getSelectedCell() {
            return selected_cell;
        }

        public Font getLetterFont() {
            double dist = Math.sqrt(Math.pow(cell_size.getWidth(), 2) + Math.pow(cell_size.getHeight(), 2));
            return new Font("Arial", (int)(dist/2.0));
        }

        private Dimension2D measureText(Font f, String text) {
            javafx.scene.text.Text theText = new javafx.scene.text.Text(text);
            theText.setFont(f);

            return new Dimension2D(theText.getBoundsInLocal().getWidth(),
                    theText.getBoundsInLocal().getHeight());
        }

        private double snap(double val) {
            // TODO: Refer to https://stackoverflow.com/a/27847190 . This allows sharper lines by drawing it in 'middle of pixel'
            return (int)val + 0.5;
        }

        private void fillCell(GraphicsContext c, Point cell, Color color) {
            if (cell == null)
                return;

            c.setFill(color);
            Point2D cell_xy = toCell(cell);
            System.out.println(c.getLineWidth());
            c.fillRect(snap(cell_xy.getX()) + line_width,
                    snap(cell_xy.getY()) + line_width,
                    snap(cell_size.getWidth()) - line_width * 2,
                    snap(cell_size.getHeight()) - line_width * 2);

            drawBorders(c);
        }

        private void shallowPaint(Point new_selected_cell) {
            GraphicsContext c = getGraphicsContext2D();
            Point old_selected_cell = selected_cell;
            selected_cell = new_selected_cell;

            refreshCell(c, old_selected_cell);
            refreshCell(c, selected_cell);
        }

        public void repaint() {
            deepPaint();
        }

        private void deepPaint() {
            GraphicsContext c = this.getGraphicsContext2D();
            // adjust cell sizes due to change in window size
            cell_size = new Dimension2D(getWidth() / num_cols, getHeight() / num_rows);
            c.setFont(getLetterFont());
            c.setFill(Color.BLACK);

            System.out.println("DEEP PAINTING");

            c.setFill(Color.WHITE);
            c.fillRect(0, 0, getWidth(), getHeight());

            // for the selected one
            refreshCell(c, selected_cell);

            // draw letters
            for (Point cell : letters.keySet()) {
                drawLetter(cell, c);
            }

            c.setLineWidth(line_width);
            c.setStroke(Color.BLACK);
            drawGrid(c);
        }

        private void refreshCell(GraphicsContext c, Point cell) {
            if (cell == null)
                return;

            Point2D cell_xy = toCell(cell);

            fillCell(c, cell, cell.equals(selected_cell) ? select_color : Color.WHITE);
            c.setFill(Color.BLACK);
            drawLetter(cell, c);
        }

        private void drawLetter(Point cell, GraphicsContext c) {
            if (letters.get(cell) == null)
                return;

            Point2D cell_xy = toCell(cell);
            Dimension2D char_size = measureText(c.getFont(), letters.get(cell).toString());

            double x = cell_xy.getX() + (cell_size.getWidth() - char_size.getWidth())/2.0;
            double y = cell_xy.getY() + cell_size.getHeight()/2.0 + char_size.getHeight()/3;

            //System.out.println("WRITE: " + cell + "\t\t" + new Point2D(x,y));

            c.fillText(letters.get(cell).toString(), x, y);
        }

        private void drawGrid(GraphicsContext c) {
            for (int x = 0; x < num_rows; x++) {
                c.strokeLine(snap(x * cell_size.getWidth()), 0,
                        snap(x * cell_size.getWidth()), getHeight());
            }

            for (int y = 0; y < num_cols; y++) {
                c.strokeLine(0, snap(y * cell_size.getHeight()),
                        getWidth(), snap(y * cell_size.getHeight()));
            }

            drawBorders(c);
        }

        private void drawBorders(GraphicsContext c) {
            // faces of the table need bolder lines
            c.setLineWidth(line_width * 2);
            c.strokeLine(0, 0,0, getHeight());
            c.strokeLine(getWidth(), 0, getWidth(), getHeight());
            c.strokeLine(0, 0, getWidth(), 0);
            c.strokeLine(0, getHeight(), getWidth(), getHeight());
        }
    }
}