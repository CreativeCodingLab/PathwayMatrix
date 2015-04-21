package org.biopax.paxtools.pattern.constraint;

import org.biopax.paxtools.pattern.MappedConst;

/**
 * Logical OR of several PathConstraints.
 *
 * @author Ozgun Babur
 */
public class MultiPathConstraint extends OR
{
	/**
	 * Constructor with specifier string of the path constraints.
	 * @param paths constructor strings for the path constraints
	 */
	public MultiPathConstraint(String ... paths)
	{
		con = new MappedConst[paths.length];

		for (int i = 0; i < con.length; i++)
		{
			con[i] = new MappedConst(new PathConstraint(paths[i]), 0, 1);
		}
	}
}
