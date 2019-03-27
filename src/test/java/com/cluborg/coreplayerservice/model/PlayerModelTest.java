package com.cluborg.coreplayerservice.model;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class PlayerModelTest {

    @Test
    public void shouldConstructTeam(){
        Player player = new Player( "mongoId", "Colm", "Murphy","colm@username.com", "teamId");
        Assert.assertNotNull(player);

        Assert.assertEquals("mongoId", player.getId());
        Assert.assertThat(player.getFirstName(), Matchers.equalToIgnoringCase("Colm"));
    }

}
