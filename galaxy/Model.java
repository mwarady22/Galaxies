package galaxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static galaxy.Place.pl;


/** The state of a Galaxies Puzzle.  Each cell, cell edge, and intersection of
 *  edges has coordinates (x, y). For cells, x and y are positive and odd.
 *  For intersections, x and y are even.  For horizontal edges, x is odd and
 *  y is even.  For vertical edges, x is even and y is odd.  On a board
 *  with w columns and h rows of cells, (0, 0) indicates the bottom left
 *  corner of the board, and (2w, 2h) indicates the upper right corner.
 *  If (x, y) are the coordinates of a cell, then (x-1, y) is its left edge,
 *  (x+1, y) its right edge, (x, y-1) its bottom edge, and (x, y+1) its
 *  top edge.  The four cells (x, y), (x+2, y), (x, y+2), and (x+2, y+2)
 *  meet at intersection (x+1, y+1).  Cells contain nonnegative integer
 *  values, or "marks". A cell containing 0 is said to be unmarked.
 *  @author Michaela Warady
 */
class Model {
    /** Stores centers. */
    private ArrayList<Place> centers;
    /** Returns centers. */
    public ArrayList<Place> getCenters() {
        return centers;
    }

    /** Stores boundaries. */
    private boolean[][] boundaries;
    /** Returns boundaries. */
    public boolean[][] getBoundaries() {
        return boundaries;
    }

    /** Stores marks. */
    private int[][] marks;
    /** Returns marks. */
    public int[][] getMarks() {
        return marks;
    }

    /** Stores cols. */
    private int vert;
    /** Returns vert. */
    public int getVert() {
        return vert;
    }

    /** Stores rows. */
    private int horiz;
    /** Returns horiz. */
    public int getHoriz() {
        return horiz;
    }

    /** The default number of squares on a side of the board. */
    static final int DEFAULT_SIZE = 7;

    /** Initializes an empty puzzle board of size DEFAULT_SIZE x DEFAULT_SIZE,
     *  with a boundary around the periphery. */
    Model() {
        init(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /** Initializes an empty puzzle board of size COLS x ROWS, with a boundary
     *  around the periphery. */
    Model(int cols, int rows) {
        init(cols, rows);
    }

    /** Initializes a copy of MODEL. */
    Model(Model model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Model model) {
        if (model == this) {
            return;
        } else {
            init(model.vert, model.horiz);

            for (Place m : model.centers) {
                this.centers.add(pl(m.x, m.y));
            }

            for (int x = 0; x < model.xlim(); x += 1) {
                for (int y = 0; y < model.ylim(); y += 1) {
                    this.boundaries[x][y] = model.boundaries[x][y];
                }
            }

            for (int i = 0; i < model.xlim(); i += 1) {
                for (int j = 0; j < model.ylim(); j += 1) {
                    this.marks[i][j] = model.marks[i][j];
                }
            }

        }
    }
    /** Initiates outer edges to boundaries. */
    void edger() {
        if ((xlim() % 2 == 1) && (ylim() % 2 == 1)) {
            for (int x = 1; x < xlim(); x += 2) {
                this.boundaries[x][0] = true;
                this.boundaries[x][ylim() - 1] = true;
            }
            for (int y = 1; y < ylim(); y += 2) {
                this.boundaries[0][y] = true;
                this.boundaries[xlim() - 1][y] = true;
            }
        }
    }

    /** Sets the puzzle board size to COLS x ROWS, and clears it. */
    void init(int cols, int rows) {
        this.vert = cols;
        this.horiz = rows;
        this.centers = new ArrayList<Place>();
        this.boundaries = new boolean[xlim()][ylim()];
        edger();
        this.marks = new int[xlim()][ylim()];
    }

    /** Clears the board (removes centers, boundaries that are not on the
     *  periphery, and marked cells) without resizing. */
    void clear() {
        init(cols(), rows());
    }

    /** Returns the number of columns of cells in the board. */
    int cols() {
        return xlim() / 2;
    }

    /** Returns the number of rows of cells in the board. */
    int rows() {
        return ylim() / 2;
    }

    /** Returns the number of vertical edges and cells in a row. */
    int xlim() {
        return (this.vert * 2) + 1;
    }

    /** Returns the number of horizontal edges and cells in a column. */
    int ylim() {
        return (this.horiz * 2) + 1;
    }

    /** Returns true iff (X, Y) is a valid cell. */
    boolean isCell(int x, int y) {
        return 0 <= x && x < xlim() && 0 <= y && y < ylim()
            && x % 2 == 1 && y % 2 == 1;
    }

    /** Returns true iff P is a valid cell. */
    boolean isCell(Place p) {
        return isCell(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a valid edge. */
    boolean isEdge(int x, int y) {
        return 0 <= x && x < xlim() && 0 <= y && y < ylim() && x % 2 != y % 2;
    }

    /** Returns true iff P is a valid edge. */
    boolean isEdge(Place p) {
        return isEdge(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a vertical edge. */
    boolean isVert(int x, int y) {
        return isEdge(x, y) && x % 2 == 0;
    }

    /** Returns true iff P is a vertical edge. */
    boolean isVert(Place p) {
        return isVert(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a horizontal edge. */
    boolean isHoriz(int x, int y) {
        return isEdge(x, y) && y % 2 == 0;
    }

    /** Returns true iff P is a horizontal edge. */
    boolean isHoriz(Place p) {
        return isHoriz(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a valid intersection. */
    boolean isIntersection(int x, int y) {
        return x % 2 == 0 && y % 2 == 0
            && x >= 0 && y >= 0 && x < xlim() && y < ylim();
    }

    /** Returns true iff P is a valid intersection. */
    boolean isIntersection(Place p) {
        return isIntersection(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a center. */
    boolean isCenter(int x, int y) {
        return this.centers.contains(pl(x, y));
    }

    /** Returns true iff P is a center. */
    boolean isCenter(Place p) {
        return isCenter(p.x, p.y);
    }

    /** Returns true iff (X, Y) is a boundary. */
    boolean isBoundary(int x, int y) {
        return this.boundaries[x][y];
    }

    /** Returns true iff P is a boundary. */
    boolean isBoundary(Place p) {
        return isBoundary(p.x, p.y);
    }

    /** Returns true iff the puzzle board is solved, given the centers and
     *  boundaries that are currently on the board. */
    boolean solved() {
        int total;
        total = 0;
        for (Place c : centers()) {
            HashSet<Place> r = findGalaxy(c);
            if (r == null) {
                return false;
            } else {
                total += r.size();
            }
        }
        return total == rows() * cols();
    }

    /** Finds cells reachable from CELL and adds them to REGION.  Specifically,
     *  it finds cells that are reachable using only vertical and horizontal
     *  moves starting from CELL that do not cross any boundaries and
     *  do not touch any cells that were initially in REGION. Requires
     *  that CELL is a valid cell. */
    private void accreteRegion(Place cell, HashSet<Place> region) {
        assert isCell(cell);
        if (region.contains(cell)) {
            return;
        }
        region.add(cell);
        for (int i = 0; i < 4; i += 1) {
            int dx0 = (i % 2) * (2 * (i / 2) - 1),
                dy0 = ((i + 1) % 2) * (2 * (i / 2) - 1);
            if (!isBoundary(cell.move(dx0, dy0))
                    && !isCenter(cell.move(dx0, dy0))) {
                accreteRegion(cell.move(2 * dx0, 2 * dy0), region);
            }
        }
    }

    /** Returns true iff REGION is a correctly formed galaxy. A correctly formed
     *  galaxy has the following characteristics:
     *      - is symmetric about CENTER,
     *      - contains no interior boundaries, and
     *      - contains no other centers.
     * Assumes that REGION is connected. */
    private boolean isGalaxy(Place center, HashSet<Place> region) {
        for (Place cell : region) {
            if (!region.contains(opposing(center, cell))
                    || (isCenter(cell) && cell != center)) {
                return false;
            }
            for (int i = 0; i < 4; i += 1) {
                int dx = (i % 2) * (2 * (i / 2) - 1),
                    dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
                Place boundary = cell.move(dx, dy),
                    nextCell = cell.move(2 * dx, 2 * dy);

                if (isBoundary(boundary) && region.contains(nextCell)) {
                    return false;
                }

                if (isCenter(boundary) && center != boundary) {
                    return false;
                }
            }
            for (int i = 0; i < 4; i += 1) {
                int dx = 2 * (i / 2) - 1,
                    dy = 2 * (i % 2) - 1;
                Place intersection = cell.move(dx, dy);
                if (isCenter(intersection) && intersection != center) {
                    return false;
                }
            }
        }
        return true;
    }


    /** Returns the galaxy containing CENTER that has the following
     *  characteristics:
     *      - encloses CENTER completely,
     *      - is symmetric about CENTER,
     *      - is connected,
     *      - contains no stray boundary edges, and
     *      - contains no other centers aside from CENTER.
     *  Otherwise, returns null. Requires that CENTER is not on the
     *  periphery. */
    HashSet<Place> findGalaxy(Place center) {
        HashSet<Place> galaxy = new HashSet<>();
        for (Place z : samemarkedContaining(center)) {
            accreteRegion(z, galaxy);
        }
        if (isGalaxy(center, galaxy)) {
            return galaxy;
        } else {
            return null;
        }
    }

    /** Returns a list of all cells "containing" PLACE if all of the cells are
     *  marked the same. A cell, c, "contains" PLACE if
     *      - c is PLACE itself,
     *      - PLACE is a corner of c, or
     *      - PLACE is an edge of c.
     *  Otherwise, returns an empty list. */
    List<Place> samemarkedContaining(Place place) {
        if (isCell(place)) {
            return asList(place);
        } else if (isVert(place)) {
            if (this.marks[place.x - 1][place.y]
                    == this.marks[place.x + 1][place.y]) {
                return asList(place.move(-1, 0), place.move(1, 0));
            }
        } else if (isHoriz(place)) {
            if (this.marks[place.x][place.y - 1]
                    == this.marks[place.x][place.y + 1]) {
                return asList(place.move(0, -1), place.move(0, 1));
            }
        } else {
            int w = mark(place.move(-1, -1));
            int x = mark(place.move(-1, 1));
            int y = mark(place.move(1, -1));
            int z = mark(place.move(1, 1));
            if (!(w == x && x == y && y == z)) {
                return Collections.emptyList();
            }
            return asList(place.move(-1, -1), place.move(-1, 1),
                    place.move(1, -1), place.move(1, 1));
        }
        return Collections.emptyList();
    }

    /** Returns the largest, unmarked region around CENTER with the
     *  following characteristics:
     *      - contains all cells touching CENTER,
     *      - consists only of unmarked cells,
     *      - is symmetric about CENTER, and
     *      - is contiguous.
     *  The method ignores boundaries and other centers on the current board.
     *  If there is no such region, returns the empty set. */
    Set<Place> maxUnmarkedRegion(Place center) {
        HashSet<Place> region = new HashSet<>();
        region.addAll(unmarkedContaining(center));
        markAll(region, 1);
        ArrayList<Place> regionList = new ArrayList<Place>(region);
        ArrayList<Place> newRegionList = new ArrayList<Place>(regionList);
        newRegionList.addAll(unmarkedSymAdjacent(center, regionList));
        while (regionList != newRegionList) {
            regionList = newRegionList;
            markAll(region, 1);
            newRegionList.addAll(unmarkedSymAdjacent(center, regionList));
        }
        region = new HashSet<Place>(regionList);
        markAll(region, 0);
        return region;
    }

    /** Marks all properly formed galaxies with value V. Unmarks all cells that
     *  are not contained in any of these galaxies. Requires that V is greater
     *  than or equal to 0. */
    void markGalaxies(int v) {
        assert v >= 0;
        markAll(0);
        for (Place c : centers()) {
            HashSet<Place> region = findGalaxy(c);
            if (region != null) {
                markAll(region, v);
            }
        }
    }

    /** Toggles the presence of a boundary at the edge (X, Y). That is, negates
     *  the value of isBoundary(X, Y) (from true to false or vice-versa).
     *  Requires that (X, Y) is an edge. */
    void toggleBoundary(int x, int y) {
        assert isEdge(x, y);
        if (this.boundaries[x][y]) {
            this.boundaries[x][y] = false;
        } else {
            this.boundaries[x][y] = true;
        }
    }

    /** Places a center at (X, Y). Requires that X and Y are within bounds of
     *  the board. */
    void placeCenter(int x, int y) {
        this.centers.add(pl(x, y));
    }

    /** Places center at P. */
    void placeCenter(Place p) {
        if (this.centers.contains(p)) {
            return;
        } else {
            placeCenter(p.x, p.y);
        }
    }

    /** Returns the current mark on cell (X, Y), or -1 if (X, Y) is not a valid
     *  cell address. */
    int mark(int x, int y) {
        if (!isCell(x, y)) {
            return -1;
        }
        return this.marks[x][y];
    }

    /** Returns the current mark on cell P, or -1 if P is not a valid cell
     *  address. */
    int mark(Place p) {
        return mark(p.x, p.y);
    }

    /** Marks the cell at (X, Y) with value V. Requires that V must be greater
     *  than or equal to 0, and that (X, Y) is a valid cell address. */
    void mark(int x, int y, int v) {
        if (!isCell(x, y)) {
            throw new IllegalArgumentException("bad cell coordinates");
        }
        if (v < 0) {
            throw new IllegalArgumentException("bad mark value");
        }
        this.marks[x][y] = v;
    }

    /** Marks the cell at P with value V. Requires that V must be greater
     *  than or equal to 0, and that P is a valid cell address. */
    void mark(Place p, int v) {
        mark(p.x, p.y, v);
    }

    /** Sets the marks of all cells in CELLS to V. Requires that V must be
     *  greater than or equal to 0. */
    void markAll(Collection<Place> cells, int v) {
        assert v >= 0;
        for (Place cell : cells) {
            mark(cell, v);
        }
    }

    /** Sets the marks of all cells to V. Requires that V must be greater than
     *  or equal to 0. */
    void markAll(int v) {
        assert v >= 0;
        ArrayList<Place> group = new ArrayList<Place>();
        for (int a = 1; a < xlim(); a += 2) {
            for (int b = 1; b < ylim(); b += 2) {
                group.add(pl(a, b));
            }
        }
        markAll(group, v);
    }

    /** Returns the position of the cell that is opposite P using P0 as the
     *  center, or null if that is not a valid cell address. */
    Place opposing(Place p0, Place p) {
        int xdist = p0.x - p.x;
        int ydist = p0.y - p.y;
        if (!isCell(p0.x + xdist, p0.y + ydist)) {
            return null;
        } else {
            return pl(p0.x + xdist, p0.y + ydist);
        }
    }

    /** Returns a list of all cells "containing" PLACE if all of the cells are
     *  unmarked. A cell, c, "contains" PLACE if
     *      - c is PLACE itself,
     *      - PLACE is a corner of c, or
     *      - PLACE is an edge of c.
     *  Otherwise, returns an empty list. */
    List<Place> unmarkedContaining(Place place) {
        if (isCell(place)) {
            if (this.marks[place.x][place.y] == 0) {
                return asList(place);
            }
        } else if (isVert(place)) {
            if (this.marks[place.x - 1][place.y] == 0
                    && this.marks[place.x + 1][place.y] == 0) {
                return asList(place.move(-1, 0), place.move(1, 0));
            }
        } else if (isHoriz(place)) {
            if (this.marks[place.x][place.y - 1] == 0
                    && this.marks[place.x][place.y + 1] == 0) {
                return asList(place.move(0, -1), place.move(0, 1));
            }
        } else {
            for (int i = 0; i < 4; i += 1) {
                int dx = 0;
                int dy = 0;
                if (i == 0) {
                    dx = -1;
                    dy = -1;
                } else if (i == 1) {
                    dx = -1;
                    dy = 1;
                } else if (i == 2) {
                    dx = 1;
                    dy = -1;
                } else if (i == 3) {
                    dx = 1;
                    dy = 1;
                }
                if (mark(place.move(dx, dy)) != 0) {
                    return Collections.emptyList();
                }
            }
            return asList(place.move(-1, -1), place.move(-1, 1),
                    place.move(1, -1), place.move(1, 1));
        }
        return Collections.emptyList();
    }

    /** Returns a list of all cells, c, such that:
     *      - c is unmarked,
     *      - The opposite cell from c relative to CENTER exists and
     *        is unmarked, and
     *      - c is vertically or horizontally adjacent to a cell in REGION.
     *  CENTER and all cells in REGION must be valid cell positions.
     *  Each cell appears at most once in the resulting list. */
    List<Place> unmarkedSymAdjacent(Place center, List<Place> region) {
        ArrayList<Place> result = new ArrayList<>();
        for (Place r : region) {
            assert isCell(r);
            for (int i = 0; i < 4; i += 1) {
                int dx = (i % 2) * (2 * (i / 2) - 1);
                int dy = ((i + 1) % 2) * (2 * (i / 2) - 1);
                Place p = r.move(2 * dx, 2 * dy);
                Place opp = opposing(center, p);
                if (opp != null) {
                    if (mark(p) == 0 && mark(opp) == 0) {
                        result.add(p);
                    }
                }
            }

        }
        return result;
    }

    /** Returns an unmodifiable view of the list of all centers. */
    List<Place> centers() {
        return Collections.unmodifiableList(this.centers);
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        int w = xlim(), h = ylim();
        for (int y = h - 1; y >= 0; y -= 1) {
            for (int x = 0; x < w; x += 1) {
                boolean cent = isCenter(x, y);
                boolean bound = isBoundary(x, y);
                if (isIntersection(x, y)) {
                    out.format(cent ? "o" : " ");
                } else if (isCell(x, y)) {
                    if (cent) {
                        out.format(mark(x, y) > 0 ? "O" : "o");
                    } else {
                        out.format(mark(x, y) > 0 ? "*" : " ");
                    }
                } else if (y % 2 == 0) {
                    if (cent) {
                        out.format(bound ? "O" : "o");
                    } else {
                        out.format(bound ? "=" : "-");
                    }
                } else if (cent) {
                    out.format(bound ? "O" : "o");
                } else {
                    out.format(bound ? "I" : "|");
                }
            }
            out.format("%n");
        }
        return out.toString();
    }


}
