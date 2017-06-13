db.fundhistoryprices.aggregate([
    { $group: { 
        _id: {"sedol":"$sedol", "isin":"$isin", "ftSymbol":"$ftSymbol"},
        count: { $sum: 1 }
    }},
    { $sort : {count:-1} }
 ]
)
 