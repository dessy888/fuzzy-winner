db.funds.find().snapshot().forEach(
    function (elem) {
    db.funds.update(
            {
                "$and": [
                    { priceBuy: null }, 
                    { _id: elem._id }
                        ]
            },
            {
            $set: {
                priceBuy: elem.price_buy
                }
            }
        );
    }
);