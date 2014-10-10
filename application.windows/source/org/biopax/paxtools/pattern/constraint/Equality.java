package org.biopax.paxtools.pattern.constraint;

import org.biopax.paxtools.pattern.Match;

/**
 * Checks identity of two elements.
 * Size = 2.
 * Checks if e1 == e2.
 *
 * @author Ozgun Babur
 */
public class Equality extends ConstraintAdapter
{
	/**
	 * Desired output.
	 */
	private boolean equals;

	/**
	 * Constructor with the desired output.
	 * @param equals the desired output
	 */
	public Equality(boolean equals)
	{
		super(2);
		this.equals = equals;
	}

	/**
	 * Checks if the two elements are identical or not identical as desired.
	 * @param match current pattern match
	 * @param ind mapped indices
	 * @return true if identity checks equals the desired value
	 */
	@Override
	public boolean satisfies(Match match, int... ind)
	{
		assert ind.length == 2;

		return (match.get(ind[0]) == match.get(ind[1])) == equals;
	}
}
