db.funds.aggregate([
    { $sort: { sedol: 1, updated: 1 } },
    { $group: { 
        _id: "$sedol",
        lastUpdated: { $last: "$$ROOT" }
    }},
    { $project: { _id: 0, 'sedol': '$lastUpdated.sedol', 'name': '$lastUpdated.name', 'unitType': '$lastUpdated.unitType', 
                    'loaded': '$lastUpdated.loaded', 'company': '$lastUpdated.company', 'sector': '$lastUpdated.sector', 
                    'plusFund': '$lastUpdated.plusFund', 'price_sell': '$lastUpdated.price_sell', 'price_buy': '$lastUpdated.price_buy', 
                    'price_change': '$lastUpdated.price_change', 'yield': '$lastUpdated.yield', 'initialCharge': '$lastUpdated.initialCharge',
                    'annualCharge': '$lastUpdated.annualCharge', 'annualSaving': '$lastUpdated.annualSaving', 
                    'netAnnualCharge': '$lastUpdated.netAnnualCharge', 'discountedCode': '$lastUpdated.discountedCode', 
                    'perf12m': '$lastUpdated.perf12m', 'perf12t24m': '$lastUpdated.perf12t24m', 'perf24t36m': '$lastUpdated.perf24t36m',
                    'perf36t48m': '$lastUpdated.perf36t48m', 'perf48t60m': '$lastUpdated.perf48t60m', 'fundSize': '$lastUpdated.fundSize', 
                    'incomeFrequency': '$lastUpdated.incomeFrequency', 'paymentType': '$lastUpdated.paymentType', 
                    'numHoldings': '$lastUpdated.numHoldings', 'updated': '$lastUpdated.updated'} },
    { $sort: { updated: 1 } }
 ])