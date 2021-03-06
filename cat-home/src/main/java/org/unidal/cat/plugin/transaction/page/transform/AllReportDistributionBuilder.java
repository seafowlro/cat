package org.unidal.cat.plugin.transaction.page.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.unidal.cat.plugin.transaction.model.entity.Bu;
import org.unidal.cat.plugin.transaction.model.entity.DistributionInName;
import org.unidal.cat.plugin.transaction.model.entity.DistributionInType;
import org.unidal.cat.plugin.transaction.model.entity.DomainStat;
import org.unidal.cat.plugin.transaction.model.entity.TransactionReport;
import org.unidal.cat.plugin.transaction.page.Model;
import org.unidal.cat.plugin.transaction.page.transform.DistributionDetailVisitor.DistributionDetail;
import org.unidal.lookup.annotation.Named;

@Named(type = AllReportDistributionBuilder.class)
public class AllReportDistributionBuilder {

	public void buildAllReportDistributionInfo(Model model, String type, String name, String ip, TransactionReport report) {
		List<DistributionDetailVisitor.DistributionDetail> distributionDetails = new ArrayList<DistributionDetailVisitor.DistributionDetail>();
		if (name == null || name.length() == 0) {
			if (report.getDistributionInTypes().size() > 0) {
				DistributionInType istributionInType = report.findDistributionInType(type);
				if (istributionInType != null) {
					Bu bu = istributionInType.findBu(ip);
					addDistributionDetail(distributionDetails, bu);
				}
			}
		} else {
			if (report.getDistributionInTypes().size() > 0) {
				DistributionInType distributionInType = report.findDistributionInType(type);
				if (distributionInType != null) {
					DistributionInName distributionInName = distributionInType.findDistributionInName(name);
					if (distributionInName != null) {
						Bu bu = distributionInName.findBu(ip);
						addDistributionDetail(distributionDetails, bu);
					}
				}
			}
		}

		Collections.sort(distributionDetails, new Comparator<DistributionDetailVisitor.DistributionDetail>() {
			@Override
			public int compare(DistributionDetailVisitor.DistributionDetail o1,
			      DistributionDetailVisitor.DistributionDetail o2) {
				long gap = o2.getTotalCount() - o1.getTotalCount();

				if (gap > 0) {
					return 1;
				} else if (gap < 0) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		model.setDistributionDetails(distributionDetails);
	}

	private void addDistributionDetail(List<DistributionDetail> distributionDetails, Bu bu) {
		if (bu != null) {
			for (DomainStat domainStat : bu.getTypeDomainCounts().values()) {
				DistributionDetail distributionDetail = new DistributionDetail();
				distributionDetail.setIp(domainStat.getDomain());
				distributionDetail.setTotalCount(domainStat.getTotalCount());
				distributionDetail.setFailCount(domainStat.getFailCount());
				distributionDetail.setMin(domainStat.getMin());
				distributionDetail.setMax(domainStat.getMax());
				distributionDetail.setAvg(domainStat.getAvg());
				distributionDetail.setQps(domainStat.getTps());
				distributionDetails.add(distributionDetail);
			}
		}
	}
}
