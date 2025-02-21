package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.model.Instruments;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JsonParserService {


    public List<Instruments> getParsedInstrumentsFromAPI(String responseBody) {
        List<Instruments> list = Arrays.stream(responseBody.split("\\n"))
                .map(this::parseInstrument)
                .collect(Collectors.toList());

        return list;

    }

    private Instruments parseInstrument(String line) {
            String[] fields = line.split("\\|");
            Instruments instrument = new Instruments();

            // ! Populate instrument fields from fields array
            instrument.setExchangeSegment(fields[0]);
            instrument.setExchangeInstrumentID(fields[1]);
            instrument.setInstrumentType(fields[2]);
            instrument.setName(fields[3]);
            instrument.setDescription(fields[4]);
            instrument.setSeries(fields[5]);
            instrument.setNameWithSeries(fields[6]);
            instrument.setInstrumentID(fields[7]);
            instrument.setPriceBandHigh((fields[8]));
            instrument.setPriceBandLow((fields[9]));
            instrument.setFreezeQty((fields[10]));
            instrument.setTickSize((fields[11]));
            instrument.setLotSize(Integer.parseInt(fields[12]));
            instrument.setMultiplier(Integer.parseInt(fields[13]));
            instrument.setUnderlyingInstrumentId(fields[14]);
            instrument.setUnderlyingIndexName(fields[15]);

            instrument.setContractExpiration(fields[16]);
            if (fields[5].equals("FUTSTK") || fields[5].equals("FUTIDX")) {
                instrument.setStrikePrice("");
                instrument.setOptionType("");
                instrument.setDisplayName(fields[17]);
                instrument.setPriceNumerator(Integer.parseInt(fields[18]));
                instrument.setPriceDenominator(Integer.parseInt(fields[19]));
                instrument.setDetailedDescription(fields[20]);
            } else {
                instrument.setStrikePrice((fields[17]));
                instrument.setOptionType(fields[18]);
                instrument.setDisplayName(fields[19]);
                instrument.setPriceNumerator(Integer.parseInt(fields[20]));
                instrument.setPriceDenominator(Integer.parseInt(fields[21]));
                instrument.setDetailedDescription(fields[22]);
            }

            return instrument;
    }
}
