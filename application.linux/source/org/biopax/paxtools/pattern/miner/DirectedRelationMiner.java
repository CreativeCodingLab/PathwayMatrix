package org.biopax.paxtools.pattern.miner;

import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level3.Protein;
import org.biopax.paxtools.model.level3.ProteinReference;
import org.biopax.paxtools.pattern.Match;
import org.biopax.paxtools.pattern.Pattern;
import org.biopax.paxtools.pattern.constraint.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.biopax.paxtools.pattern.constraint.ConBox.*;

/**
 * Miner for the directed relations between proteins.
 * @author Ozgun Babur
 */
public class DirectedRelationMiner extends MinerAdapter
{
	/**
	 * Constructor that sets name and description.
	 */
	public DirectedRelationMiner()
	{
		super("directed-relations", "Finds relations between proteins where " +
			"the first one is controlling an interaction where the second protein is participant.");
	}

	/**
	 * Constructs the pattern.
	 * @return pattern
	 */
	@Override
	public Pattern constructPattern()
	{
		Pattern p = new Pattern(ProteinReference.class, "controller PR");
		p.add(isHuman(), "controller PR");
		p.add(erToPE(), "controller PR", "controller simple PE");
		p.add(linkToComplex(), "controller simple PE", "controller PE");
		p.add(peToControl(), "controller PE", "Control");
		p.add(controlToInter(), "Control", "Interaction");
		p.add(new NOT(participantER()), "Interaction", "controller PR");
		p.add(participant(), "Interaction", "affected PE");
		p.add(linkToSimple(), "affected PE", "affected simple PE");
		p.add(new Type(Protein.class), "affected simple PE");
		p.add(peToER(), "affected simple PE", "affected PR");

		return p;
	}

	/**
	 * Writes the result as "A B", where A and B are gene symbols, and whitespace is tab.
	 * @param matches pattern search result
	 * @param out output stream
	 */
	@Override
	public void writeResult(Map<BioPAXElement, List<Match>> matches, OutputStream out)
		throws IOException{
		writeResultAsSIF(matches, out, true, getSourceLabel(), getTargetLabel());
	}

	/**
	 * Sets header of the output.
	 * @return header
	 */
	@Override
	public String getHeader()
	{
		return "Upstream\tDownstream";
	}

	public String getSourceLabel()
	{
		return "controller PR";
	}

	public String getTargetLabel()
	{
		return "affected PR";
	}

	@Override
	public String[] getMediatorLabels()
	{
		return new String[]{"Control", "Interaction"};
	}
}
