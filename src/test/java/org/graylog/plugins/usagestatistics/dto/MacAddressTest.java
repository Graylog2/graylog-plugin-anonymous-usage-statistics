/**
 * This file is part of Graylog Anonymous Usage Statistics Plugin.
 *
 * Graylog Anonymous Usage Statistics Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog Anonymous Usage Statistics Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog Anonymous Usage Statistics Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.usagestatistics.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MacAddressTest {
    @Test
    public void macAddressOnlyContainsFirstThreeOctets() throws Exception {
        MacAddress macAddress = MacAddress.create("00:00:de:ad:be:ef");
        assertThat(macAddress.macAddress()).isEqualTo("00:00:de");
    }

    @Test
    public void emptyMacAddressIsEmpty() throws Exception {
        MacAddress macAddress = MacAddress.EMPTY;
        assertThat(macAddress.macAddress()).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createThrowsIllegalArgumentException() throws Exception {
        MacAddress.create("HAHA!");
    }
}
