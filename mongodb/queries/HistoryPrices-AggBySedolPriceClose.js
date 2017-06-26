db.historyprices.aggregate([
    { $sort: { sedol: 1 , price_close: 1} },
    { $group: { 
        _id: "$sedol",
        count: { $sum: 1 }
    }}
 ], 
    {allowDiskUse : true}
)
 