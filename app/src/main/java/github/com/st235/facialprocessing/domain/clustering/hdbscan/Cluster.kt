package github.com.st235.facialprocessing.domain.clustering.hdbscan

class Cluster(
    private val id: Int,
    val label: Int,
    val parent: Cluster?,
    private val birthLevel: Double,
    private var numPoints: Int
) {

    private var deathLevel: Double
    private var propagatedStability: Double
    private var numConstraintsSatisfied: Int
    private var propagatedNumConstraintsSatisfied: Int
    private val virtualChildCluster = mutableSetOf<Int>()

    val propagatedDescendants = mutableListOf<Cluster>()
    var propagatedLowestChildDeathLevel: Double
    var stability: Double
    var hasChildren: Boolean

    //First level where points with this cluster's label appear.
    var hierarchyPosition: Int

    init {
        deathLevel = 0.0
        propagatedStability = 0.0
        numConstraintsSatisfied = 0
        propagatedNumConstraintsSatisfied = 0
        hierarchyPosition = 0
        stability = 0.0
        propagatedLowestChildDeathLevel = Double.MAX_VALUE
        hasChildren = false
        if (parent != null) {
            parent.hasChildren = true
        }
    }

    fun detachPoints(numPoints: Int, level: Double) {
        this.numPoints -= numPoints
        stability += (numPoints * (1 / level - 1 / birthLevel))

        if (this.numPoints == 0) {
            this.deathLevel = level
        }

        if (this.numPoints < 0) {
            throw IllegalStateException("Cluster cannot have less than 0 points.")
        }
    }

    fun propagate() {
        if (parent != null) {
            if (propagatedLowestChildDeathLevel == Double.MAX_VALUE) {
                propagatedLowestChildDeathLevel = deathLevel;
            }
            if (propagatedLowestChildDeathLevel < parent.propagatedLowestChildDeathLevel) {
                parent.propagatedLowestChildDeathLevel = propagatedLowestChildDeathLevel
            }
            if (!hasChildren) {
                parent.propagatedNumConstraintsSatisfied += numConstraintsSatisfied
                parent.propagatedStability += stability
                parent.propagatedDescendants.add(this)
            } else if (numConstraintsSatisfied > propagatedNumConstraintsSatisfied) {
                parent.propagatedNumConstraintsSatisfied += numConstraintsSatisfied
                parent.propagatedStability += stability
                parent.propagatedDescendants.add(this)
            } else if (numConstraintsSatisfied < propagatedNumConstraintsSatisfied) {
                parent.propagatedNumConstraintsSatisfied += propagatedNumConstraintsSatisfied
                parent.propagatedStability += propagatedStability
                parent.propagatedDescendants.addAll(propagatedDescendants)
            } else {
                // numConstraintsSatisfied == propagatedNumConstraintsSatisfied
                //Chose the parent over descendants if there is a tie in stability:
                if (stability >= propagatedStability) {
                    parent.propagatedNumConstraintsSatisfied += numConstraintsSatisfied
                    parent.propagatedStability += stability
                    parent.propagatedDescendants.add(this)
                } else {
                    parent.propagatedNumConstraintsSatisfied += propagatedNumConstraintsSatisfied
                    parent.propagatedStability += propagatedStability
                    parent.propagatedDescendants.addAll(propagatedDescendants)
                }
            }
        }
    }

    fun addPointsToVirtualChildCluster(points: Set<Int>) {
        virtualChildCluster.addAll(points)
    }

    fun virtualChildClusterConstraintsPoint(point: Int): Boolean {
        return virtualChildCluster.contains(point)
    }

    fun addVirtualChildConstraintsSatisfied(numConstraints: Int) {
        propagatedNumConstraintsSatisfied += numConstraints
    }

    fun addConstraintsSatisfied(numConstraints: Int) {
        numConstraintsSatisfied += numConstraints
    }

    fun releaseVirtualChildCluster() {
        virtualChildCluster.clear()
    }

    fun getClusterId(): Int {
        return id
    }
}