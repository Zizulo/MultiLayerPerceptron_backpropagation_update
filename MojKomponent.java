import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

public class MojKomponent extends JComponent {
    private final int CELL_SIZE = 40;
    private boolean [][] grid = new boolean[12][12];

    public MojKomponent() {
        setPreferredSize(new Dimension(12 * CELL_SIZE, 12 * CELL_SIZE));
        
        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                updateGrid(e);
            }
        });
        
        addMouseMotionListener(new MouseInputAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateGrid(e);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(int x = 0; x < 12; x++) {
            for(int y = 0; y < 12; y++) {
                g.setColor(grid[x][y] ? Color.BLACK : Color.WHITE);
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void updateGrid(MouseEvent e) {
        int x = e.getX() / CELL_SIZE;
        int y = e.getY() / CELL_SIZE;

        if(x >= 0 && x < 12 && y >= 0 && y < 12) {
            grid[x][y] = true;
            repaint();
        }
    }

    public void clearGrid() {
        for(int x = 0; x < 12; x++) {
            Arrays.fill(grid[x], false);
        }
        repaint();
    }

    public double[] getGridAsInput() {
        double[] input = new double[144];
        int index = 0;

        for(int y = 0; y < 12; y++) {
            for(int x = 0; x < 12; x++) {
                input[index++] = grid[x][y] ? 1.0 : 0.0;
            }
        }

        return input;
    }
}
