package com.trading.safetrade_simulator.utility;

import com.trading.safetrade_simulator.model.Instruments;
import com.trading.safetrade_simulator.model.QuotesData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Helper {
    public List<Instruments> mapData(List<Instruments> list, Map<Integer, QuotesData> map) {

            List<Instruments> resultList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                int id = Integer.parseInt(list.get(i).getExchangeInstrumentID());
                if (map.containsKey(id)) {
                    Instruments ins = list.get(i);
                    ins.setQuotesData(map.get(id));
                    resultList.add(ins);
                }
            }
            return resultList;

    }
}
