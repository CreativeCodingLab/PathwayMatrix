package org.biopax.paxtools.pattern.miner;

import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level3.SmallMoleculeReference;
import org.biopax.paxtools.model.level3.XReferrable;
import org.biopax.paxtools.model.level3.Xref;
import org.biopax.paxtools.pattern.util.HGNC;

/**
 * Tries to get gene symbols for genes and display names for small molecules.
 *
 * @author Ozgun Babur
 */
public class CommonIDFetcher implements IDFetcher
{
	@Override
	public String fetchID(BioPAXElement ele)
	{
		if (ele instanceof SmallMoleculeReference)
		{
			SmallMoleculeReference smr = (SmallMoleculeReference) ele;
			if (smr.getDisplayName() != null) return smr.getDisplayName();
			else if (!smr.getName().isEmpty()) return smr.getName().iterator().next();
			else return null;
		}
		else if (ele instanceof XReferrable)
		{
			for (Xref xr : ((XReferrable) ele).getXref())
			{
				String db = xr.getDb();
				if (db != null)
				{
					db = db.toLowerCase();
					if (db.startsWith("hgnc"))
					{
						String id = xr.getId();
						if (id != null)
						{
							String symbol = HGNC.getSymbol(id);
							if (symbol != null && !symbol.isEmpty())
							{
								return symbol;
							}
						}
					}
				}
			}
		}

		return null;
	}
}
