/*
 * Copyright 2017 John Grosh (john.a.grosh@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.discordipc.entities;

/**
 * A encapsulation of a Discord User's data provided when a
 * {@link com.jagrosh.discordipc.IPCListener IPCListener} fires
 * {@link com.jagrosh.discordipc.IPCListener#onActivityJoinRequest(com.jagrosh.discordipc.IPCClient, String, User)
 * onActivityJoinRequest}.
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class User {
	private final String name;
	private final String discriminator;
	private final long id;
	private final String avatar;

	/**
	 * Constructs a new {@link User}.<br>
	 * Only implemented internally.
	 *
	 * @param name          user's name
	 * @param discriminator user's discrim
	 * @param id            user's id
	 * @param avatar        user's avatar hash, or {@code null} if they have no avatar
	 */
	public User(String name, String discriminator, long id, String avatar) {
		this.name = name;
		this.discriminator = discriminator;
		this.id = id;
		this.avatar = avatar;
	}

	/**
	 * Gets the Users account name.
	 *
	 * @return The Users account name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the Users discriminator.
	 *
	 * @return The Users discriminator.
	 */
	public String getDiscriminator() {
		return discriminator;
	}

	/**
	 * Gets the Users Snowflake ID as a {@code String}.
	 *
	 * @return The Users Snowflake ID as a {@code String}.
	 */
	public String getId() {
		return Long.toString(id);
	}

	/**
	 * Gets the Users avatar ID.
	 *
	 * @return The Users avatar ID.
	 */
	public String getAvatarId() {
		return avatar;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof User))
			return false;
		User oUser = (User) o;
		return this == oUser || this.id == oUser.id;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public String toString() {
		return "U:" + getName() + '(' + id + ')';
	}
}
