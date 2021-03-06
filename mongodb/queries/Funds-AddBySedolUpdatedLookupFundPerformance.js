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
                    'numHoldings': '$lastUpdated.numHoldings', 'updated': '$lastUpdated.updated', 
                    'key': {$concat: ["$lastUpdated.sedol", "-", {$dateToString: { format: "%d/%m/%Y", date: '$lastUpdated.updated' }}]}}},
    { $sort: { sedol: 1 } },
    { $lookup: {
           from: "fundperformance",
           localField: "key",
           foreignField: "key",
           as: "perf" } },
    { $unwind: "$perf" },
    { $project: { sedol: 1, 'isin': '$perf.isin', 'ftSymbol': '$perf.isin', name: 1, unitType: 1, loaded: 1, company: 1, sector: 1, plusFund: 1, price_sell: 1, price_buy: 1, 
        price_change: 1, yield: 1, initialCharge: 1, annualCharge: 1, annualSaving: 1, netAnnualCharge: 1,  
        fundSize: 1, incomeFrequency: 1, paymentType: 1, numHoldings: 1,  
        'reportName': '$perf.reportName', '1D': '$perf._1D', '3D': '$perf._3D',
        '5D': '$perf._5D', '1W': '$perf._1W', '2W': '$perf._2W', '3W': '$perf._3W', '1M': '$perf._1M', '2M': '$perf._2M',
        '3M': '$perf._3M', '4M': '$perf._4M', '5M': '$perf._5M', '6M': '$perf._6M', '7M': '$perf._7M', '8M': '$perf._8M',
        '9M': '$perf._9M', '10M': '$perf._10M', '11M': '$perf._11M', '1Y': '$perf._1Y', '2Y': '$perf._2Y', '3Y': '$perf._3Y',
        '4Y': '$perf._4Y', '5Y': '$perf._5Y', '6Y': '$perf._6Y', '7Y': '$perf._7Y', '8Y': '$perf._8Y', '9Y': '$perf._9Y',
        '10Y': '$perf._10Y', '11Y': '$perf._11Y', '12Y': '$perf._12Y', '13Y': '$perf._13Y', '14Y': '$perf._14Y', '15Y': '$perf._15Y',
        '16Y': '$perf._16Y', '17Y': '$perf._17Y', '18Y': '$perf._18Y', '19Y': '$perf._19Y', '20Y': '$perf._20Y', 'ALL': '$perf._ALL',
        perf12m: 1, perf12t24m: 1, perf24t36m: 1, perf36t48m: 1, perf48t60m: 1, 
        'cobDate': { $dateToString: { format: "%d/%m/%Y", date: '$perf.cobDate' } },
        updated: { $dateToString: { format: "%d/%m/%Y", date: '$updated' } },
        discountedCode: 1 } }    
], 
    {
        allowDiskUse: true,
        cursor: { batchSize: 1000000 }
    }
)